package me.ikosarim.cripto_bot.service;

import me.ikosarim.cripto_bot.db_model.TradeHistoryEntity;
import me.ikosarim.cripto_bot.json_model.UserTradeEntity;
import me.ikosarim.cripto_bot.repos.TradeHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;

@Service
@Transactional
public class TradeHistoryServiceImpl implements TradeHistoryService {

    @Autowired
    TradeHistoryRepository tradeHistoryRepository;

    @Override
    public void saveTrade(UserTradeEntity userTradeEntity) {
        Timestamp timestamp = new Timestamp(userTradeEntity.getDate());
        Date date = new Date(timestamp.getTime());
        TradeHistoryEntity tradeHistoryEntity = TradeHistoryEntity.builder()
                .tradeId(userTradeEntity.getTradeId())
                .date(date)
                .currencyPair(userTradeEntity.getPair())
                .price(userTradeEntity.getPrice())
                .quantity(userTradeEntity.getQuantity())
                .amount(userTradeEntity.getAmount())
                .tradeType(userTradeEntity.getType())
                .build();
        tradeHistoryRepository.save(tradeHistoryEntity);
    }
}