package me.ikosarim.cripto_bot.containers;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TradeObject {

    private String pairName;

    private Double percent;
    private Double uppestBorder;
    private Double upperBorder;
    private Double lowerBorder;
    private Double lowestBorder;

    private Integer maxOrdersCount;

    private String quantity;
    private Double orderBookDelta;

    private Double tradePrice;
    private Double orderBookAskPrice;
    private Double orderBookBidPrice;
}