package me.ikosarim.cripto_bot.tasks;

import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.json_model.OpenOrderEntity;
import me.ikosarim.cripto_bot.json_model.OrderBookEntity;
import me.ikosarim.cripto_bot.json_model.OrderCancelStatus;
import me.ikosarim.cripto_bot.json_model.OrderCreateStatus;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import static java.lang.Double.parseDouble;

@Component
@Scope("prototype")
public class ReplaceOrderInGlassTask implements Runnable {

    private TradeObject tradeObject;
    private Integer orderId;
    private String tradeType;
    private Double tradeQuantity;

    @Autowired
    SendRequestsService sendRequestsService;
    @Autowired
    private Map<String, ScheduledFuture<ReplaceOrderInGlassTask>> scheduledFutureMap;

    @Override
    public void run() {
        while (tradeObject == null || orderId == null || tradeType == null) {
            return;
        }
        Map<String, List<OpenOrderEntity>> userOpenOrders = sendRequestsService.sendGetOpenOrders();
        String tradePairName = tradeObject.getPairName();
        List<OpenOrderEntity> openOrderEntityListForCurrentPair = userOpenOrders.get(tradePairName);
        ScheduledFuture<ReplaceOrderInGlassTask> taskFuture = scheduledFutureMap.get(tradePairName);
        if (openOrderEntityListForCurrentPair == null) {
//            save statistic
//            Publish that trade is complete (logging)
            cancelAndRemoveTask(taskFuture);
        }
        OpenOrderEntity openOrderEntityForThisTask = openOrderEntityListForCurrentPair.stream()
                .filter(order -> orderId.toString().equals(order.getOrderId()))
                .findFirst()
                .orElse(null);
        if (openOrderEntityForThisTask == null) {
//            save statistic
//            Publish that trade is complete (logging)
            cancelAndRemoveTask(taskFuture);
        }
        if (parseDouble(openOrderEntityForThisTask.getQuantity()) < tradeQuantity) {
            tradeQuantity = parseDouble(openOrderEntityForThisTask.getQuantity());
//            save statistic
        }
        OrderBookEntity orderBookEntity = sendRequestsService.sendGetOrderBookRequest(tradePairName);
        Double priceInGlass;
        double priceToTrade = 0.0;
        if ("buy".equals(tradeType)) {
            if (tradeObject.getOrderBookBidPrice().toString().equals(orderBookEntity.getBid()[0][0])) {
                return;
            }
            priceInGlass = parseDouble(orderBookEntity.getBid()[0][0]);
            priceToTrade = priceInGlass + tradeObject.getOrderBookDeltaPrice();
        } else if ("sell".equals(tradeType)) {
            if (tradeObject.getOrderBookAskPrice().toString().equals(orderBookEntity.getAsk()[0][0])) {
                return;
            }
            priceInGlass = parseDouble(orderBookEntity.getAsk()[0][0]);
            priceToTrade = priceInGlass - tradeObject.getOrderBookDeltaPrice();
        } else {
//            Print in telegram chat and print in log about error (logging)
            cancelAndRemoveTask(taskFuture);
        }
        replaceOrderToTopInGlass(priceToTrade, taskFuture);
    }

    private void replaceOrderToTopInGlass(double priceToTrade, ScheduledFuture<ReplaceOrderInGlassTask> taskFuture) {
        cancelOrder(taskFuture);
        createOrder(priceToTrade, taskFuture);
    }

    private void createOrder(double priceToTrade, ScheduledFuture<ReplaceOrderInGlassTask> taskFuture) {
        final Double finalPriceToTrade = priceToTrade;
        Map<String, Object> createOrderArguments = new HashMap<>() {{
            put("pair", tradeObject.getPairName());
            put("quantity", tradeQuantity);
            put("price", finalPriceToTrade);
            put("type", tradeType);
        }};
        OrderCreateStatus orderCreateStatus = sendRequestsService.sendOrderCreateRequest(createOrderArguments);
        if (!orderCreateStatus.isResult()) {
//            Publish that error and error cause (logging)
            cancelAndRemoveTask(taskFuture);
        }
        if ("buy".equals(tradeType)) {
            tradeObject.setOrderBookBidPrice(finalPriceToTrade);
        } else if ("sell".equals(tradeType)) {
            tradeObject.setOrderBookAskPrice(finalPriceToTrade);
        }
        orderId = orderCreateStatus.getOrderId();
    }

    private void cancelOrder(ScheduledFuture<ReplaceOrderInGlassTask> taskFuture) {
        Map<String, Object> cancelOrderArguments = new HashMap<>() {{
            put("order_id", orderId);
        }};
        OrderCancelStatus orderCancelStatus = sendRequestsService.sendOrderCancelRequest(cancelOrderArguments);
        if (!orderCancelStatus.isResult()) {
//            Publish that error and error cause (logging)
            cancelAndRemoveTask(taskFuture);
        }
    }

    private void cancelAndRemoveTask(ScheduledFuture<ReplaceOrderInGlassTask> taskFuture) {
        taskFuture.cancel(true);
        scheduledFutureMap.remove(taskFuture);
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setTradeObject(TradeObject tradeObject) {
        this.tradeObject = tradeObject;
        this.tradeQuantity = tradeObject.getQuantity();
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
