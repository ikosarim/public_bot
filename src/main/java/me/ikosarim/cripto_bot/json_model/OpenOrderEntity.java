package me.ikosarim.cripto_bot.json_model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OpenOrderEntity {

    @JsonProperty(value = "order_id")
    private String orderId;
    @JsonProperty(value = "created")
    private String created;
    @JsonProperty(value = "type")
    private String type;
    @JsonProperty(value = "pair")
    private String pair;
    @JsonProperty(value = "price")
    private String price;
    @JsonProperty(value = "quantity")
    private String quantity;
    @JsonProperty(value = "amount")
    private String amount;
}
