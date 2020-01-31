package me.ikosarim.cripto_bot.tasks;

import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.json_model.OrderCreateStatus;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;

public class ScalpingAlgorithmTask implements Runnable {

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
        tradeObjectMap.forEach((pairName, tradeObject) -> actualPairTradePrice.forEach((name, actualPrice) -> {
            if (name.equals(pairName)) {
                if (actualPrice > tradeObject.getUpperBorder()
                        && actualPrice < tradeObject.getUppestBorder()) {
                    cancelTrendTask(tradeObject, "Bear_");
                    workInScalpingTradeCorridor(tradeObject, actualPrice, actualPrice < tradeObject.getActualTradePrice(),
                            tradeObject.isSellOrder(), "sell", true, false);
                } else if (actualPrice < tradeObject.getLowerBorder()
                        && actualPrice > tradeObject.getLowestBorder()) {
                    cancelTrendTask(tradeObject, "Bull_");
                    workInScalpingTradeCorridor(tradeObject, actualPrice, actualPrice > tradeObject.getActualTradePrice(),
                            tradeObject.isBuyOrder(), "buy", false, true);
                } else if (actualPrice > tradeObject.getLowerBorder()
                        && actualPrice < tradeObject.getUpperBorder()) {
                    workInMainCorridor(tradeObject, actualPrice);
                } else if (actualPrice > tradeObject.getUppestBorder()) {
                    createOrderForTrade(tradeObject, actualPrice, "buy", "Bull_");
                    createNewTradeConditions(pairName, tradeObject);
                } else if (actualPrice < tradeObject.getLowestBorder()) {
                    createOrderForTrade(tradeObject, actualPrice, "sell", "Bear_");
                    createNewTradeConditions(pairName, tradeObject);
                }
            }
        }));
    }

    private void cancelTrendTask(TradeObject tradeObject, String bear_) {
        ScheduledFuture<ReplaceOrderInGlassTask> taskFuture = scheduledFutureMap.get(bear_ + tradeObject.getPairName());
        if (taskFuture != null) {
            taskFuture.cancel(true);
            scheduledFutureMap.remove(taskFuture);
        }
    }

    private void createNewTradeConditions(String pairName, TradeObject tradeObject) {
        tradeObject.setBuyOrder(false);
        tradeObject.setSellOrder(false);
        TradeObject newBorderTradeObject = sendRequestsService.sendInitGetTradesRequest(
                pairName,
                new CurrencyPairList(singletonList(tradeObject))
        ).get(pairName);
        tradeObjectMap.put(pairName, newBorderTradeObject);
    }

    private void workInMainCorridor(TradeObject tradeObject, Double actualPrice) {
        tradeObject.setActualTradePrice(actualPrice);
        ScheduledFuture<ReplaceOrderInGlassTask> taskFuture = scheduledFutureMap.get(tradeObject.getPairName());
        if (taskFuture != null) {
            tradeObject.setBuyOrder(false);
            tradeObject.setSellOrder(false);
            taskFuture.cancel(true);
            scheduledFutureMap.remove(taskFuture);
        }
    }

    private void workInScalpingTradeCorridor(TradeObject tradeObject, Double actualPrice, boolean priceCondition,
                                             boolean buyOrder, String tradeType, boolean isSell, boolean isBuy) {
        if (priceCondition) {
            if (!buyOrder) {
                Integer orderId = createOrderForTrade(tradeObject, actualPrice, tradeType, "");
                if (orderId != null) {
                    tradeObject.setSellOrder(isSell);
                    tradeObject.setBuyOrder(isBuy);
                }
            }
        }
        tradeObject.setActualTradePrice(actualPrice);
    }

    private Integer createOrderForTrade(TradeObject tradeObject, Double actualPrice, String tradeType, String trendType) {
        Integer orderId = createOrder(tradeObject, actualPrice, tradeType);
        if (orderId != null) {
            ReplaceOrderInGlassTask task = ctx.getBean(ReplaceOrderInGlassTask.class);
            task.setOrderId(orderId);
            task.setTradeObject(tradeObject);
            task.setTradeType(tradeType);
            scheduledFutureMap.put(
                    trendType + tradeObject.getPairName(),
                    threadPoolTaskScheduler.scheduleWithFixedDelay(task, 2000)
            );
        }
        return orderId;
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
        if (!orderCreateStatus.isResult()) {
//            Publish that error and error cause
        }
        return orderCreateStatus.getOrderId();
    }
}