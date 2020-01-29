package me.ikosarim.cripto_bot.init;

import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.json_model.PairSettingEntity;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.lang.Double.parseDouble;
import static java.util.stream.Collectors.joining;

@Component
public class InitFirstTradeObjects {

    @Autowired
    private SendRequestsService sendRequestsService;
    @Autowired
    private Map<String, TradeObject> tradeObjectMap;
    @Autowired
    private Map<String, PairSettingEntity> pairSettingEntityMap;

    public void initTradeObjectMap(CurrencyPairList pairList) {
        for (TradeObject tradeObject : pairList.getPairList()) {
            String pairName = tradeObject.getPairName();
            pairName += "_USD";
            tradeObject.setPairName(pairName);
        }
        String pairsUrl = pairList.getPairList()
                .stream()
                .map(TradeObject::getPairName)
                .collect(joining(","));
        tradeObjectMap.putAll(sendRequestsService.sendInitGetTradesRequest(pairsUrl, pairList));
        pairSettingEntityMap.putAll(sendRequestsService.sendGetPairSettingsRequest());
        tradeObjectMap.forEach((key, value) -> value.setOrderBookDeltaPrice(
                pairSettingEntityMap.entrySet().stream()
                .filter(e -> key.equals(e.getKey()))
                .map(e -> parseDouble(e.getValue().getMinPrice()))
                .findFirst()
                .orElseThrow()
        ));
    }
}