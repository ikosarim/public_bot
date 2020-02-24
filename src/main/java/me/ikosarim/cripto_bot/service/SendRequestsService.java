package me.ikosarim.cripto_bot.service;

import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.json_model.*;

import java.util.List;
import java.util.Map;

public interface SendRequestsService {

    Map<String, TradeObject> sendInitGetTradesRequest(String pairs, CurrencyPairList pairList);

    Map<String, PairSettingEntity> sendGetPairSettingsRequest();

    UserInfoEntity sendPostUserInfoRequest();

    Map<String, List<OpenOrderEntity>> sendGetOpenOrders();

    OrderBookEntity sendGetOrderBookRequest(String pairName);

    OrderCancelStatus sendOrderCancelRequest(Map<String, Object> cancelOrderArguments);

    OrderCreateStatus sendOrderCreateRequest(Map<String, Object> createOrderArguments);

    Map<String, Map<String, Double>> sendGetTradesRequest(String pairUrl);

    List<UserTradeEntity> sendGetTradeResult(Map<String, Object> tradeResultArguments);

    UserInfoEntity sendCheckRequest(Map<String, String> keysMap);
}