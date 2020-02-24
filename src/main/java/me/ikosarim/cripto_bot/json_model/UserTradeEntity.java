package me.ikosarim.cripto_bot.json_model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTradeEntity {

    @JsonProperty(value = "trade_id")
    private Long tradeId;
    @JsonProperty(value = "date")
    private Long date;
    @JsonProperty(value = "type")
    private String type;
    @JsonProperty(value = "pair")
    private String pair;
    @JsonProperty(value = "order_id")
    private Long orderId;
    @JsonProperty(value = "quantity")
    private double quantity;
    @JsonProperty(value = "price")
    private double price;
    @JsonProperty(value = "amount")
    private double amount;
    
}
