package me.ikosarim.cripto_bot.json_model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCancelStatus {

    @JsonProperty(value = "result")
    private boolean result;
    @JsonProperty(value = "error")
    private String error;
}
