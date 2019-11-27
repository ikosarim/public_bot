package me.ikosarim.cripto_bot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.json_model.TradeInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Double.parseDouble;
import static org.springframework.data.util.StreamUtils.createStreamFromIterator;

@Service
public class ExmoJSonMappingServiceImpl implements JSonMappingService {

    @Autowired
    Map<String, Map<String, TradeObject>> tradeInfoEntityMap;

    @Override
    public void insertInitDataToTradeInMap(JsonNode node, CurrencyPairList pairList) {
        ObjectMapper objectMapper = new ObjectMapper();
        node.fields().forEachRemaining(
                entry -> {
                    String name = entry.getKey();
                    tradeInfoEntityMap.put(
                            name,
                            new HashMap<>() {{
                                put(
                                        "buy",
                                        getTradeInfoEntity(entry.getValue(), objectMapper, "buy", pairList, name)
                                );
                                put(
                                        "sell",
                                        getTradeInfoEntity(entry.getValue(), objectMapper, "sell", pairList, name)
                                );
                            }}
                    );
                });
    }

    @Override
    public void insertOrderBookDeltaInMap(JsonNode node) {
        tradeInfoEntityMap.entrySet().forEach(
                entry -> node.fields().forEachRemaining(
                        nodeEntry -> {
                            if (entry.getKey().equals(nodeEntry.getKey())) {
                                setOrderBookDelta(entry, nodeEntry, "buy");
                                setOrderBookDelta(entry, nodeEntry, "sell");
                            }
                        }
                )
        );
    }

    private void setOrderBookDelta(Map.Entry<String, Map<String, TradeObject>> entry,
                                   Map.Entry<String, JsonNode> nodeEntry, String buy) {
        entry.getValue().get(buy)
                .setOrderBookDelta(parseDouble(nodeEntry.getValue()
                        .get("min_quantity")
                        .textValue()));
    }

    private TradeObject getTradeInfoEntity(JsonNode node, ObjectMapper objectMapper, String type,
                                           CurrencyPairList pairList, String pairName) {
        return createStreamFromIterator(node.elements()).map(el -> {
            try {
                return objectMapper.treeToValue(el, TradeInfoEntity.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }).filter(tie -> type.equals(tie.getType()))
                .map(tie -> pairList.getPairList()
                        .stream()
                        .filter(pair -> pairName.equals(pair.getPairName()))
                        .map(pair -> buildFinishTradeObject(pairName, tie, pair))
                        .findFirst()
                        .orElseThrow())
                .findFirst()
                .orElseThrow();
    }

    private TradeObject buildFinishTradeObject(String pairName, TradeInfoEntity tie, TradeObject pair) {
        return TradeObject.builder()
                .pairName(pairName)
                .percent(pair.getPercent())
                .uppestBorder(createUpBorder(tie, pair, 2.0))
                .upperBorder(createUpBorder(tie, pair, 1.0))
                .lowerBorder(createLowBorder(tie, pair, 1.0))
                .lowestBorder(createLowBorder(tie, pair, 2.0))
                .maxOrdersCount(pair.getMaxOrdersCount())
                .quantity(pair.getQuantity())
                .tradePrice(parseDouble(tie.getPrice()))
                .build();
    }

    private Double createLowBorder(TradeInfoEntity tie, TradeObject pair, double v) {
        return parseDouble(tie.getPrice()) * (1.0 - pair.getPercent() * v / 100);
    }

    private Double createUpBorder(TradeInfoEntity tie, TradeObject pair, double v) {
        return parseDouble(tie.getPrice()) * (1.0 + pair.getPercent() * v / 100);
    }
}