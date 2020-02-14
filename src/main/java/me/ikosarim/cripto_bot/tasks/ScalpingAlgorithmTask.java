package me.ikosarim.cripto_bot.tasks;

import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.json_model.OrderCancelStatus;
import me.ikosarim.cripto_bot.json_model.OrderCreateStatus;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import static java.util.Collections.singletonList;

@Component
@Scope("prototype")
public class ScalpingAlgorithmTask implements Runnable {

    private String pairUrl;

    public void setPairUrl(String pairUrl) {
        this.pairUrl = pairUrl;
    }

    @Autowired
    private Map<String, TradeObject> tradeObjectMap;
    @Autowired
    private SendRequestsService sendRequestsService;
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    @Autowired
    private Map<String, ScheduledFuture<ReplaceOrderInGlassTask>> scheduledFutureMap;

    private ApplicationContext ctx;

    @Autowired
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        Map<String, Double> actualPairTradePrice = sendRequestsService.sendGetTradesRequest(pairUrl);
        tradeObjectMap.forEach((pairName, tradeObject) -> actualPairTradePrice.forEach((name, actualPrice) -> {
//            log pair name and trade price
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
                    createOrderForTrade(tradeObject, actualPrice, "buy", "Bull_", false,
                            false);
                    createNewTradeConditions(pairName, tradeObject);
                } else if (actualPrice < tradeObject.getLowestBorder()) {
                    createOrderForTrade(tradeObject, actualPrice, "sell", "Bear_", false,
                            false);
                    createNewTradeConditions(pairName, tradeObject);
                }
            }
        }));
    }

    private void workInMainCorridor(TradeObject tradeObject, Double actualPrice) {
        tradeObject.setActualTradePrice(actualPrice);
        ScheduledFuture<ReplaceOrderInGlassTask> taskFuture = scheduledFutureMap.get(tradeObject.getPairName());
        if (taskFuture != null) {
            tradeObject.setBuyOrder(false);
            tradeObject.setSellOrder(false);
            taskFuture.cancel(true);
            cancelTaskOrder(taskFuture);
            scheduledFutureMap.remove(taskFuture);
        }
    }

    private void cancelTaskOrder(ScheduledFuture<ReplaceOrderInGlassTask> taskFuture) {
        Map<String, Object> args = new HashMap<>() {{
            try {
                put("order_id", taskFuture.get().getOrderId());
            } catch (InterruptedException e) {
                e.printStackTrace(); // write in log
            } catch (ExecutionException e) {
                e.printStackTrace(); // write in log
            }
        }};
        OrderCancelStatus orderCancelStatus = sendRequestsService.sendOrderCancelRequest(args);
        if (!orderCancelStatus.isResult()) {
//            logging
        }
    }

    private void cancelTrendTask(TradeObject tradeObject, String trendType) {
        ScheduledFuture<ReplaceOrderInGlassTask> taskFuture = scheduledFutureMap.get(trendType + tradeObject.getPairName());
        if (taskFuture != null) {
            taskFuture.cancel(true);
            cancelTaskOrder(taskFuture);
            scheduledFutureMap.remove(taskFuture);
        }
    }

    private void createNewTradeConditions(String pairName, TradeObject tradeObject) {
        TradeObject newBorderTradeObject = sendRequestsService.sendInitGetTradesRequest(
                pairName,
                new CurrencyPairList(singletonList(tradeObject))
        ).get(pairName);
        newBorderTradeObject.setOrderBookDeltaPrice(tradeObject.getOrderBookDeltaPrice());
        tradeObjectMap.put(pairName, newBorderTradeObject);
    }

    private void workInScalpingTradeCorridor(TradeObject tradeObject, Double actualPrice, boolean priceCondition,
                                             boolean alreadyExecuteTask, String tradeType, boolean isSell, boolean isBuy) {
        if (priceCondition) {
            if (!alreadyExecuteTask) {
                createOrderForTrade(tradeObject, actualPrice, tradeType, "", isSell, isBuy);
            }
        }
        tradeObject.setActualTradePrice(actualPrice);
    }

    private Integer createOrderForTrade(TradeObject tradeObject, Double actualPrice, String tradeType, String trendType,
                                        boolean isSell, boolean isBuy) {
        Integer orderId = createOrder(tradeObject, actualPrice, tradeType);
        if (orderId != null) {
            ReplaceOrderInGlassTask task = ctx.getBean(ReplaceOrderInGlassTask.class);
            task.setOrderId(orderId);
            task.setTradeObject(tradeObject);
            task.setTradeType(tradeType);
            tradeObject.setSellOrder(isSell);
            tradeObject.setBuyOrder(isBuy);
            scheduledFutureMap.put(
                    trendType + tradeObject.getPairName(),
                    (ScheduledFuture<ReplaceOrderInGlassTask>) threadPoolTaskScheduler.scheduleWithFixedDelay(task, 2000)
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
//            Publish that error and error cause (logging)
            return null;
        }
        if ("buy".equals(orderType)) {
            tradeObject.setOrderBookBidPrice(finalPriceToTrade);
        } else if ("sell".equals(orderType)) {
            tradeObject.setOrderBookAskPrice(finalPriceToTrade);
        }
        return orderCreateStatus.getOrderId();
    }
}