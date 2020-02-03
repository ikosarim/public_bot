package me.ikosarim.cripto_bot.tasks;

import lombok.Setter;
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
@Setter
public class ReplaceOrderInGlassTask implements Runnable {

    private TradeObject tradeObject;
    private Integer orderId;
    private String tradeType;

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
//            Publish that trade is complete
            cancelAndRemoveTask(taskFuture);
        }
        OpenOrderEntity openOrderEntityForThisTask = openOrderEntityListForCurrentPair.stream()
                .filter(order -> orderId.toString().equals(order.getOrderId()))
                .findFirst()
                .orElse(null);
        if (openOrderEntityForThisTask == null) {
//            Publish that trade is complete
            cancelAndRemoveTask(taskFuture);
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
//            Print in telegram chat and print in log about error
            cancelAndRemoveTask(taskFuture);
        }
        Double qty = parseDouble(openOrderEntityForThisTask.getQuantity());
        replaceOrderToTopInGlass(priceToTrade, qty, taskFuture);
    }

    private void replaceOrderToTopInGlass(double priceToTrade, Double qty,
                                          ScheduledFuture<ReplaceOrderInGlassTask> taskFuture) {
        cancelOrder(taskFuture);
        createOrder(priceToTrade, qty, taskFuture);
    }

    private void createOrder(double priceToTrade, Double qty, ScheduledFuture<ReplaceOrderInGlassTask> taskFuture) {
        final Double finalPriceToTrade = priceToTrade;
        Map<String, Object> createOrderArguments = new HashMap<>() {{
            put("pair", tradeObject.getPairName());
            put("quantity", qty);
            put("price", finalPriceToTrade);
            put("type", tradeType);
        }};
        OrderCreateStatus orderCreateStatus = sendRequestsService.sendOrderCreateRequest(createOrderArguments);
        if (!orderCreateStatus.isResult()){
//            Publish that error and error cause
            cancelAndRemoveTask(taskFuture);
        }
        if ("buy".equals(tradeType)){
            tradeObject.setOrderBookBidPrice(finalPriceToTrade);
        } else if ("sell".equals(tradeType)){
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
//            Publish that error and error cause
            cancelAndRemoveTask(taskFuture);
        }
    }

    private void cancelAndRemoveTask(ScheduledFuture<ReplaceOrderInGlassTask> taskFuture) {
        taskFuture.cancel(true);
        scheduledFutureMap.remove(taskFuture);
    }
}
