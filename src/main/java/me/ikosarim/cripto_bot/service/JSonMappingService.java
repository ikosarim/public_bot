package me.ikosarim.cripto_bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;

import java.util.Map;

public interface JSonMappingService {

    Map<String, Map<String, TradeObject>> insertInitDataToTradeInfoMap(JsonNode node, CurrencyPairList pairList);
}
