package me.ikosarim.cripto_bot.tasks;

import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.json_model.OrderCreateStatus;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import static java.util.stream.Collectors.joining;

public class ScalpingAlgorithmTask implements Runnable{
    @Autowired
    private Map<String, TradeObject> tradeObjectMap;
    @Autowired
    private SendRequestsService sendRequestsService;
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    @Autowired
    private Map<String, ScheduledFuture> scheduledFutureMap;
    @Autowired
    private ApplicationContext ctx;

    private String pairUrl = tradeObjectMap.values()
            .stream().map(TradeObject::getPairName)
            .collect(joining());

    @Override
    public void run() {
        Map<String, Double> actualPairTradePrice = sendRequestsService.sendGetTradesRequest(pairUrl);
        tradeObjectMap.forEach((pairName, tradeObject) -> {
            actualPairTradePrice.forEach((name, actualPrice) -> {
                if (name.equals(pairName)){
                    if (actualPrice > tradeObject.getUpperBorder()
                    && actualPrice < tradeObject.getUppestBorder()){
                        workInScalpingTradeCorridor(tradeObject, actualPrice, actualPrice < tradeObject.getActualTradePrice(),
                                tradeObject.isSellOrder(), "sell", true, false);
                    } else if (actualPrice < tradeObject.getLowerBorder()
                    && actualPrice > tradeObject.getLowestBorder()){
                        workInScalpingTradeCorridor(tradeObject, actualPrice, actualPrice > tradeObject.getActualTradePrice(),
                                tradeObject.isBuyOrder(), "buy", false, true);
                    } else if (actualPrice > tradeObject.getLowerBorder()
                    && actualPrice < tradeObject.getUpperBorder()){
                        workInMainCorridor(tradeObject, actualPrice);
                    }
                }
            });
        });
    }

    // TODO: 31.01.2020 Удалять из мапы future после остановки таска

    private void workInMainCorridor(TradeObject tradeObject, Double actualPrice) {
        tradeObject.setActualTradePrice(actualPrice);
        if (scheduledFutureMap.get(tradeObject.getPairName()) != null){
            tradeObject.setBuyOrder(false);
            tradeObject.setSellOrder(false);
            scheduledFutureMap.get(tradeObject.getPairName()).cancel(true);
        }
    }

    private void workInScalpingTradeCorridor(TradeObject tradeObject, Double actualPrice, boolean priceCondition,
                                             boolean buyOrder, String tradeType, boolean isSell, boolean isBuy) {
        if (priceCondition) {
            if (!buyOrder) {
                Integer orderId = createOrder(tradeObject, actualPrice, tradeType);
                tradeObject.setSellOrder(isSell);
                tradeObject.setBuyOrder(isBuy);
                ReplaceOrderInGlassTask task = (ReplaceOrderInGlassTask) ctx.getBean("ReplaceOrderInGlassTask");
                task.setTradeObject(tradeObject);
                task.setOrderId(orderId);
                task.setTradeType(tradeType);
                scheduledFutureMap.put(
                        tradeObject.getPairName(),
                        threadPoolTaskScheduler.scheduleWithFixedDelay(task, 2000)
                );
            }
        } else {
            tradeObject.setActualTradePrice(actualPrice);
        }
    }

    private Integer createOrder(TradeObject tradeObject, Double actualPrice, String orderType) {
        final Double finalPriceToTrade = actualPrice;
        Map<String, Object> createOrderArguments = new HashMap<>() {{
            put("pair", tradeObject.getPairName());
            put("quantity", tradeObject.getQuantity());
            put("price", finalPriceToTrade);
            put("type", orderType);
        }};
        OrderCreateStatus orderCreateStatus = sendRequestsService.sendOrderCreateRequest(createOrderArguments);
        if (!orderCreateStatus.isResult()){
//            Publish that error and error cause
        }
        return orderCreateStatus.getOrderId();
    }
}
