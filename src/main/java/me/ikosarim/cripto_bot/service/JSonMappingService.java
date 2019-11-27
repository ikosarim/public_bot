package me.ikosarim.cripto_bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import me.ikosarim.cripto_bot.containers.CurrencyPairList;

public interface JSonMappingService {

    void insertInitDataToTradeInMap(JsonNode node, CurrencyPairList pairList);

    void insertOrderBookDeltaInMap(JsonNode node);
}