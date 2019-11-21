package me.ikosarim.cripto_bot.init;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.json_model.TradeInfoEntity;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static org.springframework.data.util.StreamUtils.createStreamFromIterator;

@Component
public class InitFirstTradeObjects {

    @Autowired
    SendRequestsService sendRequestsService;

    public void init(CurrencyPairList pairList) {
        String pairsUrl = pairList.getPairList()
                .stream()
                .map(TradeObject::getPairName)
                .map(pairName -> pairName + "_USD")
                .collect(joining(","));
        JsonNode node = sendRequestsService.sendGetTradesRequest(pairsUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, TradeInfoEntity>> tradeInfoMap = new HashMap<>();
        node.fields().forEachRemaining(
                entry -> tradeInfoMap.put(
                        entry.getKey(),
                        new HashMap<>() {{
                            put(
                                    "buy",
                                    getTradeInfoEntity(entry.getValue(), objectMapper, "buy")
                            );
                            put(
                                    "sell",
                                    getTradeInfoEntity(entry.getValue(), objectMapper, "sell")
                            );
                        }}
                )
        );
        tradeInfoMap.entrySet().forEach(
                tie -> System.out.println(tie.toString())
        );
    }

    private TradeInfoEntity getTradeInfoEntity(JsonNode node, ObjectMapper objectMapper, String type) {
        return createStreamFromIterator(
                node.elements()
        ).map(el -> {
            try {
                return objectMapper.treeToValue(el, TradeInfoEntity.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }).filter(tie -> type.equals(tie.getType()))
                .findFirst()
                .orElseThrow();
    }
}
