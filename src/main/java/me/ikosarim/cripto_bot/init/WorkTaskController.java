package me.ikosarim.cripto_bot.init;

import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.json_model.PairSettingEntity;
import me.ikosarim.cripto_bot.json_model.UserInfoEntity;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import me.ikosarim.cripto_bot.tasks.ReplaceOrderInGlassTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import static java.lang.Double.parseDouble;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

@Component
public class WorkTaskController {

    private Logger logger = LoggerFactory.getLogger(WorkTaskController.class);

    private ScheduledFuture<ScalpingAlgorithmTask> scalpingAlgorithmFuture;

    public ScheduledFuture<ScalpingAlgorithmTask> getScalpingAlgorithmFuture() {
        return scalpingAlgorithmFuture;
    }

    @Autowired
    private SendRequestsService sendRequestsService;

    @Autowired
    private Map<String, TradeObject> tradeObjectMap;
    @Autowired
    private Map<String, PairSettingEntity> pairSettingEntityMap;
    @Autowired
    private Map<String, ScheduledFuture<ReplaceOrderInGlassTask>> scheduledFutureMap;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;
    @Autowired
    private ScalpingAlgorithmTask scalpingAlgorithmTask;

    public void startTrade(CurrencyPairList pairList) {
        for (TradeObject tradeObject : pairList.getPairList()) {
            String pairName = tradeObject.getPairName();
            pairName += "_USD";
            tradeObject.setPairName(pairName);
        }
        String pairsUrl = pairList.getPairList()
                .stream()
                .map(TradeObject::getPairName)
                .collect(joining(","));
        logger.info("Start trade, trade pairs are - " + pairsUrl);
        tradeObjectMap.putAll(sendRequestsService.sendInitGetTradesRequest(pairsUrl, pairList));
        pairSettingEntityMap.putAll(sendRequestsService.sendGetPairSettingsRequest());
        tradeObjectMap.forEach((key, value) -> value.setOrderBookDeltaPrice(
                pairSettingEntityMap.entrySet().stream()
                        .filter(e -> key.equals(e.getKey()))
                        .map(e -> parseDouble(e.getValue().getMinPrice()))
                        .findFirst()
                        .orElseThrow()
        ));
        logger.info("TradeObjectMap is full");
        scalpingAlgorithmTask.setPairUrl(pairsUrl);
        scalpingAlgorithmFuture = (ScheduledFuture<ScalpingAlgorithmTask>) taskScheduler.scheduleWithFixedDelay(scalpingAlgorithmTask, 2000);
        logger.info("Create and start scalping algorithm task");
    }

    public void stopTrade() {
        tradeObjectMap.forEach((k, v) -> tradeObjectMap.remove(v));
        pairSettingEntityMap.forEach((k, v) -> pairSettingEntityMap.remove(v));
        logger.info("Clean all bean maps");
        scheduledFutureMap.forEach((k, v) -> {
                    v.cancel(true);
                    scheduledFutureMap.remove(v);
                }
        );
        scalpingAlgorithmFuture.cancel(true);
        logger.info("Stop all tasks and remove from task map");
        logger.info("Stop trade");
    }

    public UserInfoEntity getUserStatistic() {
        UserInfoEntity userInfoEntity = sendRequestsService.sendPostUserInfoRequest();

        Map<String, String> balanceMap = userInfoEntity.getBalances();
        balanceMap = balanceMap.entrySet()
                .stream()
                .filter(e -> !"0".equals(e.getValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, String> reserveMap = userInfoEntity.getReserved();
        reserveMap = reserveMap.entrySet()
                .stream()
                .filter(e -> !"0".equals(e.getValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        userInfoEntity.setBalances(balanceMap);
        userInfoEntity.setReserved(reserveMap);

        logger.info("Show user info");

        return userInfoEntity;
    }

    public ObjectError validateUserKeys(Map<String, String> keyArgs) {
        UserInfoEntity userInfoEntity = sendRequestsService.sendCheckRequest(keyArgs);
        if (userInfoEntity.getUid() == null) {
            logger.error("Bad pair key - secret");
            return new ObjectError("keys map", "pair of key and secret is wrong");
        }
        return null;
    }
}