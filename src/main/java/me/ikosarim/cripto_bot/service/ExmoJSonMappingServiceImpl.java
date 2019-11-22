package me.ikosarim.cripto_bot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.ikosarim.cripto_bot.json_model.TradeInfoEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.data.util.StreamUtils.createStreamFromIterator;

@Service
public class ExmoJSonMappingServiceImpl implements JSonMappingService {

    @Override
    public Map<String, Map<String, TradeInfoEntity>> insertInitDataToTradeInfoMap(JsonNode node) {
        Map<String, Map<String, TradeInfoEntity>> tradeInfoEntityMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        node.fields().forEachRemaining(
                entry -> tradeInfoEntityMap.put(
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
                ));
        return tradeInfoEntityMap;
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
