package me.ikosarim.cripto_bot.init;

import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.stream.Collectors.joining;

public class InitFirstTradeObjects {

    @Autowired
    SendRequestsService sendRequestsService;

    public void init(CurrencyPairList pairList) {
        String pairsUrl = pairList.getPairList()
                .stream()
                .map(TradeObject::getPairName)
                .collect(joining(","));
        sendRequestsService.sendGetTradesRequest(pairsUrl);
    }
}
