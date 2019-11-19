package me.ikosarim.cripto_bot.json_model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TradeEntity {

    private String tradePairName;
    List<TradeInfoEntity> tradeInfoEntityList;
}
