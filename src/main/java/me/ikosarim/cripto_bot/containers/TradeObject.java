package me.ikosarim.cripto_bot.containers;

import lombok.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TradeObject {

    @Size(min = 5, max = 14)
    private String pairName;

    @Positive
    private Double percent;
    @Positive
    private double quantity;

    private Double uppestBorder;
    private Double upperBorder;
    private Double lowerBorder;
    private Double lowestBorder;

    private Double orderBookDeltaPrice;

    private Double tradeBuyPrice;
    private Double tradeSellPrice;
    private Double actualTradePrice;
    private Double orderBookAskPrice;
    private Double orderBookBidPrice;

    private boolean sellOrder = false;
    private boolean buyOrder = false;
}