package me.ikosarim.cripto_bot.json_model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class TradeInfoEntity {

    private Integer tradeId;
    private String type;
    private String quantity;
    private String price;
    private String amount;
    private Timestamp date;
}
