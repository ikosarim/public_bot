package me.ikosarim.cripto_bot.config;

import me.ikosarim.cripto_bot.json_model.TradeInfoEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class BusinessConfig {

    @Bean
    public Map<String, Map<String, TradeInfoEntity>> tradeInfoEntityMap() {
        return new HashMap<>();
    }
}
