package me.ikosarim.cripto_bot.json_model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.sql.Timestamp;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoEntity {

    @JsonProperty(value = "uid")
    private Long uid;
    @JsonProperty(value = "server_date")
    private Timestamp serverDate;
    @JsonProperty(value = "balances")
    private Map<String, String> balances;
    @JsonProperty(value = "reserved")
    private Map<String, String> reserved;
}