package me.ikosarim.cripto_bot.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface SendRequestsService {

    JsonNode sendGetTradesRequest(String pairs);
}
