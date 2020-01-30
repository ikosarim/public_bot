package me.ikosarim.cripto_bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.json_model.OpenOrderEntity;
import me.ikosarim.cripto_bot.json_model.PairSettingEntity;

import java.util.List;
import java.util.Map;

public interface JSonMappingService {

    Map<String, TradeObject> returnInitDataToTradeInMap(JsonNode node, CurrencyPairList pairList);

    Map<String, PairSettingEntity> convertToPairSettingEntity(JsonNode node);

    Map<String, List<OpenOrderEntity>> mapToOpenOrdersEntity(JsonNode node);
}