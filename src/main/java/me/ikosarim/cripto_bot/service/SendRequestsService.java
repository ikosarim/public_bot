package me.ikosarim.cripto_bot.service;

import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.json_model.PairSettingEntity;
import me.ikosarim.cripto_bot.json_model.UserInfoEntity;

import java.util.Map;

public interface SendRequestsService {

    Map<String, TradeObject> sendInitGetTradesRequest(String pairs, CurrencyPairList pairList);

    Map<String, PairSettingEntity> sendGetPairSettingsRequest();

    UserInfoEntity sendPostUserInfoRequest();
}