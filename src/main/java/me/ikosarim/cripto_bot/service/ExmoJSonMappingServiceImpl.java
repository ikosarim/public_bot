package me.ikosarim.cripto_bot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.json_model.PairSettingEntity;
import me.ikosarim.cripto_bot.json_model.TradeInfoEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Double.parseDouble;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.util.StreamUtils.createStreamFromIterator;

@Service
public class ExmoJSonMappingServiceImpl implements JSonMappingService {

    @Override
    public Map<String, TradeObject> returnInitDataToTradeInMap(JsonNode node, CurrencyPairList pairList) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, TradeObject> tradeObjectMap = new HashMap<>();
        node.fields().forEachRemaining(
                entry -> {
                    String name = entry.getKey();
                    tradeObjectMap.put(
                            name,
                            getTradeObject(entry.getValue(), objectMapper, pairList, name)
                    );
                });
        return tradeObjectMap;
    }

    private TradeObject getTradeObject(JsonNode node, ObjectMapper objectMapper, CurrencyPairList pairList, String name){
        List<TradeInfoEntity> tradeInfoEntityList = createStreamFromIterator(node.elements()).map(el -> {
            try {
                return objectMapper.treeToValue(el, TradeInfoEntity.class);
            } catch (JsonProcessingException e){
                e.printStackTrace();
            }
            return null;
        }).collect(toList());
        TradeInfoEntity buyTradeInfo = getFirstTradeInfoObject(tradeInfoEntityList, "buy");
        TradeInfoEntity sellTradeInfo = getFirstTradeInfoObject(tradeInfoEntityList, "sell");
        TradeObject tradeObject = pairList.getPairList()
                .stream()
                .filter(pair -> name.equals(pair.getPairName()))
                .findFirst()
                .orElseThrow();
        tradeObject.setTradeBuyPrice(parseDouble(buyTradeInfo.getPrice()));
        tradeObject.setTradeSellPrice(parseDouble(sellTradeInfo.getPrice()));
        tradeObject.setLowestBorder(createLowBorder(tradeObject, 2.0));
        tradeObject.setLowerBorder(createLowBorder(tradeObject, 1.0));
        tradeObject.setUpperBorder(createUpBorder(tradeObject, 1.0));
        tradeObject.setUppestBorder(createUpBorder(tradeObject, 2.0));
        return tradeObject;
    }

    private TradeInfoEntity getFirstTradeInfoObject(List<TradeInfoEntity> tradeInfoEntityList, String buy) {
        return tradeInfoEntityList.stream()
                .filter(tie -> buy.equals(tie.getType()))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public Map<String, PairSettingEntity> convertToPairSettingEntity(JsonNode node) {
        Map<String, PairSettingEntity> pairSettingEntityMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        node.fields().forEachRemaining(entry -> {
            String pairName = entry.getKey();
            try {
                pairSettingEntityMap.put(
                        pairName,
                        objectMapper.treeToValue(entry.getValue(), PairSettingEntity.class)
                );
            } catch (JsonProcessingException e){
                e.printStackTrace();
            }
        });
        return pairSettingEntityMap;
    }

    private Double createLowBorder(TradeObject tradeObject, double v) {
        return ((tradeObject.getTradeBuyPrice() + tradeObject.getTradeSellPrice()) / 2) * (1.0 - tradeObject.getPercent() * v / 100);
    }

    private Double createUpBorder(TradeObject tradeObject, double v) {
        return ((tradeObject.getTradeBuyPrice() + tradeObject.getTradeSellPrice()) / 2) * (1.0 + tradeObject.getPercent() * v / 100);
    }
}