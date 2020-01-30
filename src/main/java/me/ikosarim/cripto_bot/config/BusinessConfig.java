package me.ikosarim.cripto_bot.config;

import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.json_model.PairSettingEntity;
import me.ikosarim.cripto_bot.tasks.ReplaceOrderInGlassTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

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
    public Map<String, PairSettingEntity> pairSettingEntityMap() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, ScheduledFuture<ReplaceOrderInGlassTask>> scheduledFutureMap() {
        return new HashMap<>();
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(16);
        return threadPoolTaskScheduler;
    }

    @Bean
    public Map<String, ScheduledFuture> stringScheduledFutureMap(){
        return new HashMap<>();
    }
}