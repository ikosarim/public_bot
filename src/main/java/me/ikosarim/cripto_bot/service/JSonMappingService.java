package me.ikosarim.cripto_bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;

import java.util.Map;

public interface JSonMappingService {

    Map<String, Map<String, TradeObject>> insertInitDataToTradeInMap(JsonNode node, CurrencyPairList pairList);

    Map<String, Map<String, TradeObject>> insertOrderBookDeltaInMap(JsonNode node);
}
