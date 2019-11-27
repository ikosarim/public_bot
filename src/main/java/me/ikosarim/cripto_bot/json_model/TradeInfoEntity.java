package me.ikosarim.cripto_bot.json_model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TradeInfoEntity {

    @JsonProperty(value = "trade_id")
    private Integer tradeId;
    @JsonProperty(value = "type")
    private String type;
    @JsonProperty(value = "quantity")
    private String quantity;
    @JsonProperty(value = "price")
    private String price;
    @JsonProperty(value = "amount")
    private String amount;
    @JsonProperty(value = "date")
    private Timestamp date;
}