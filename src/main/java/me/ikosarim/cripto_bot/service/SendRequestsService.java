package me.ikosarim.cripto_bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import me.ikosarim.cripto_bot.json_model.UserInfoEntity;

public interface SendRequestsService {

    JsonNode sendGetTradesRequest(String pairs);

    JsonNode sendGetPairSettingsRequest();

    UserInfoEntity sendPostUserInfoRequest();
}