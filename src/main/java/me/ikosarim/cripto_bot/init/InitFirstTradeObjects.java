package me.ikosarim.cripto_bot.init;

import com.fasterxml.jackson.databind.JsonNode;
import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.service.JSonMappingService;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.joining;

@Component
public class InitFirstTradeObjects {

    @Autowired
    SendRequestsService sendRequestsService;
    @Autowired
    JSonMappingService jSonMappingService;

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
        JsonNode node = sendRequestsService.sendGetTradesRequest(pairsUrl);
        jSonMappingService.insertInitDataToTradeInMap(node, pairList);
        node = sendRequestsService.sendGetPairSettingsRequest();
        jSonMappingService.insertOrderBookDeltaInMap(node);
    }
}
