package me.ikosarim.cripto_bot.tasks;

import lombok.extern.slf4j.Slf4j;
import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.json_model.*;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import me.ikosarim.cripto_bot.service.TradeHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import static java.lang.Double.parseDouble;
import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@Scope("prototype")
public class ReplaceOrderInGlassTask implements Runnable {

    private TradeObject tradeObject;
    private Long orderId;
    private String tradeType;
    private String trendType;
    private Double tradeQuantity;

    @Autowired
    SendRequestsService sendRequestsService;
    @Autowired
    TradeHistoryService tradeHistoryService;
    @Autowired
    private Map<String, ScheduledFuture<ReplaceOrderInGlassTask>> scheduledFutureMap;

    @Override
    public void run() {
        String tradePairName = trendType + tradeObject.getPairName();
        log.info("Replace order in glass for trade - " + tradePairName);
        while (tradeObject == null || orderId == null || tradeType == null || trendType == null) {
            log.warn("Not set one of fields: tradeObject - " + tradeObject + ", orderId - " + orderId + ", tradeType - "
                    + tradeType + ", trendType - " + tradeType + " for trade - " + tradePairName);
            return;
        }
        Map<String, List<OpenOrderEntity>> userOpenOrders = sendRequestsService.sendGetOpenOrders();
        List<OpenOrderEntity> openOrderEntityListForCurrentPair = userOpenOrders.get(tradeObject.getPairName());
        ScheduledFuture<ReplaceOrderInGlassTask> taskFuture = scheduledFutureMap.get(tradePairName);
        if (openOrderEntityListForCurrentPair == null) {
            log.info("No open orders, trade is complete");
            saveStatisticData(tradeObject.getPairName());
            cancelAndRemoveTask(taskFuture);
            return;
        }
        OpenOrderEntity openOrderEntityForThisTask = openOrderEntityListForCurrentPair.stream()
                .filter(order -> orderId.toString().equals(order.getOrderId()))
                .findFirst()
                .orElse(null);
        if (openOrderEntityForThisTask == null) {
            log.info("No open orders for pair - " + tradePairName + ", trade is complete");
            saveStatisticData(tradeObject.getPairName());
            cancelAndRemoveTask(taskFuture);
            return;
        }
        if (parseDouble(openOrderEntityForThisTask.getQuantity()) < tradeQuantity) {
            tradeQuantity = parseDouble(openOrderEntityForThisTask.getQuantity());
            log.info("Trade is complete partly for " + tradePairName + ", current quantity - " + tradeQuantity);
            saveStatisticData(tradeObject.getPairName());
        }
        OrderBookEntity orderBookEntity = sendRequestsService.sendGetOrderBookRequest(tradeObject.getPairName());
        if (orderBookEntity == null) {
            return;
        }
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
            log.error("For " + tradePairName + " wrong trade type - " + tradeType);
            cancelAndRemoveTask(taskFuture);
        }
        replaceOrderToTopInGlass(priceToTrade, taskFuture);
        log.info("Actual price in glass - " + priceToTrade + ", for trade - " + tradePairName);
    }

    private void saveStatisticData(String name) {
        log.info("Save trade for " + name);
        Map<String, Object> userTradesMap = new HashMap<>() {{
            put("pair", name);
            put("limit", 5);
            put("offset", 0);
        }};
        List<UserTradeEntity> userTradeEntityList = sendRequestsService.sendGetTradeResult(userTradesMap);
        userTradeEntityList = userTradeEntityList.stream()
                .filter(ut -> orderId.equals(ut.getOrderId()))
                .collect(toList());
        userTradeEntityList.forEach(ut -> tradeHistoryService.saveTrade(ut));
    }

    private void replaceOrderToTopInGlass(double priceToTrade, ScheduledFuture<ReplaceOrderInGlassTask> taskFuture) {
        log.info("Ready for replace order for " + trendType + tradeObject.getPairName() + ", price to trade - " + priceToTrade);
        cancelOrder(taskFuture);
        createOrder(priceToTrade, taskFuture);
    }

    private void createOrder(double priceToTrade, ScheduledFuture<ReplaceOrderInGlassTask> taskFuture) {
        final Double finalPriceToTrade = priceToTrade;
        Map<String, Object> createOrderArguments = new HashMap<>() {{
            put("pair", tradeObject.getPairName());
            put("quantity", "buy".equals(tradeType) ? tradeQuantity + 0.1 * tradeQuantity : tradeQuantity);
            put("price", finalPriceToTrade);
            put("type", tradeType);
        }};
        OrderCreateStatus orderCreateStatus = sendRequestsService.sendOrderCreateRequest(createOrderArguments);
        if (!orderCreateStatus.isResult()) {
            log.error("Error in creating order for - " + trendType + tradeObject.getPairName() + ", need to cancel");
            log.error(orderCreateStatus.getError());
            cancelAndRemoveTask(taskFuture);
        }
        if ("buy".equals(tradeType)) {
            tradeObject.setOrderBookBidPrice(finalPriceToTrade);
        } else if ("sell".equals(tradeType)) {
            tradeObject.setOrderBookAskPrice(finalPriceToTrade);
        }
        orderId = orderCreateStatus.getOrderId();
        log.info("Order for - " + trendType + tradeObject.getPairName() + " create successful, order id - " + orderId);
    }

    private void cancelOrder(ScheduledFuture<ReplaceOrderInGlassTask> taskFuture) {
        Map<String, Object> cancelOrderArguments = new HashMap<>() {{
            put("order_id", orderId);
        }};
        OrderCancelStatus orderCancelStatus = sendRequestsService.sendOrderCancelRequest(cancelOrderArguments);
        if (!orderCancelStatus.isResult()) {
            log.error("Error in cancelling order for - " + trendType + tradeObject.getPairName() + ", need to cancel task");
            log.error(orderCancelStatus.getError());
            cancelAndRemoveTask(taskFuture);
        }
        log.info("Order for - " + trendType + tradeObject.getPairName() + " successful cancel");
    }

    private void cancelAndRemoveTask(ScheduledFuture<ReplaceOrderInGlassTask> taskFuture) {
        log.info("Cancel and remove task in replace order in glass task for " + trendType + tradeObject.getPairName());
        scheduledFutureMap.remove(trendType + tradeObject.getPairName(), taskFuture);
        log.info("Cancel future and remove future - " + taskFuture.toString());
        taskFuture.cancel(true);
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setTradeObject(TradeObject tradeObject) {
        this.tradeObject = tradeObject;
        this.tradeQuantity = tradeObject.getQuantity();
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setTrendType(String trendType) {
        this.trendType = trendType;
    }
}
