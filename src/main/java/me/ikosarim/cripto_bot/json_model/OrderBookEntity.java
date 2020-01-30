package me.ikosarim.cripto_bot.json_model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderBookEntity {

    @JsonProperty(value = "ask_quantity")
    private String askQuantity;
    @JsonProperty(value = "ask_amount")
    private String askAmount;
    @JsonProperty(value = "ask_top")
    private String askTop;
    @JsonProperty(value = "bid_quantity")
    private String bidQuantity;
    @JsonProperty(value = "bid_amount")
    private String bidAmount;
    @JsonProperty(value = "bid_top")
    private String bidTop;
    @JsonProperty(value = "ask")
    private String[][] ask;
    @JsonProperty(value = "bid")
    private String[][]bid;
}
