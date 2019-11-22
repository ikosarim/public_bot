package me.ikosarim.cripto_bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.json_model.TradeInfoEntity;

import java.util.Map;

public interface JSonMappingService {

    Map<String, Map<String, TradeInfoEntity>> insertInitDataToTradeInfoMap(JsonNode node, CurrencyPairList pairList);
}
