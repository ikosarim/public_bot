package me.ikosarim.cripto_bot.config;

import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.json_model.PairSettingEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class BusinessConfig {

    @Bean
    public Map<String, String> userPrivateInfoMap() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, TradeObject> tradeObjectMap() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, PairSettingEntity> pairSettingEntityMap(){
        return new HashMap<>();
    }
}