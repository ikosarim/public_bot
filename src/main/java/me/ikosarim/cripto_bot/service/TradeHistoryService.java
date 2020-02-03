package me.ikosarim.cripto_bot.service;

import me.ikosarim.cripto_bot.json_model.UserTradeEntity;

public interface TradeHistoryService {

    void saveTrade(UserTradeEntity userTradeEntity);
}
