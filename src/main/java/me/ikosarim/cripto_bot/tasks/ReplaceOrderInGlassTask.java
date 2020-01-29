package me.ikosarim.cripto_bot.tasks;

import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.json_model.OpenOrderEntity;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class ReplaceOrderInGlassTask implements Runnable {

    private TradeObject tradeObject;
    private Integer orderId;
    private String tradeType;

    @Autowired
    SendRequestsService sendRequestsService;

    @Override
    public void run() {
        while (tradeObject == null || orderId == null || tradeType == null){
            return;
        }
        Map<String, List<OpenOrderEntity>> userOpenOrders = sendRequestsService.sendGetOpenOrders();
    }
}
