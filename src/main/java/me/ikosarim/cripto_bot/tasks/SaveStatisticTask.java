package me.ikosarim.cripto_bot.tasks;

import lombok.extern.slf4j.Slf4j;
import me.ikosarim.cripto_bot.db_model.AllStatisticEntity;
import me.ikosarim.cripto_bot.json_model.UserInfoEntity;
import me.ikosarim.cripto_bot.repos.AllStatisticRepository;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class SaveStatisticTask {

    @Autowired
    AllStatisticRepository allStatisticRepository;
    @Autowired
    SendRequestsService sendRequestsService;

    @Scheduled(cron = "0 0 12 * * ? 2020")
    @Async
    public void saveStatistic() {
        UserInfoEntity userInfoEntity = sendRequestsService.sendPostUserInfoRequest();
        Map<String, Pair<String, String>> allQuantityPairMap = new HashMap<>();
        userInfoEntity.getBalances().forEach(
                (balancePairName, balanceQty) -> userInfoEntity.getReserved().forEach(
                        (reservedPairName, reserveQty) -> {
                            if (balancePairName.equals(reservedPairName)) {
                                if (!"0".equals(balanceQty)
                                        || !"0".equals(reserveQty)) {
                                    allQuantityPairMap.put(balancePairName, Pair.of(balanceQty, reserveQty));
                                }
                            }
                        }));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date date = new Date(timestamp.getTime());
        allQuantityPairMap.forEach((pairName, qty) -> {
            AllStatisticEntity entity = AllStatisticEntity.builder()
                    .date(date)
                    .currencyPair(pairName)
                    .walletQuantity(qty.getFirst())
                    .openOrdersQuantity(qty.getSecond())
                    .build();
            allStatisticRepository.save(entity);
            log.info("Saved statistic");
        });
    }
}