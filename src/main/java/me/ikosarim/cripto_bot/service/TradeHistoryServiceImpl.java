package me.ikosarim.cripto_bot.service;

import me.ikosarim.cripto_bot.json_model.UserTradeEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TradeHistoryServiceImpl implements TradeHistoryService {

    @Override
    public void saveTrade(UserTradeEntity userTradeEntity) {

    }
}