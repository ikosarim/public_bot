package me.ikosarim.cripto_bot.json_model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PairSettingEntity {

    @JsonProperty(value = "min_quantity")
    private String minQuantity;
    @JsonProperty(value = "max_quantity")
    private String maxQuantity;
    @JsonProperty(value = "min_price")
    private String minPrice;
    @JsonProperty(value = "max_price")
    private String maxPrice;
    @JsonProperty(value = "max_amount")
    private String maxAmount;
    @JsonProperty(value = "min_amount")
    private String minAmount;
}
