package me.ikosarim.cripto_bot.containers;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TradeObject {

    private String pairName;

    private BigDecimal percent;
    private BigDecimal uppestBorder;
    private BigDecimal upperBorder;
    private BigDecimal lowerBorder;
    private BigDecimal lowestBorder;

    private Integer maxOrdersCount;

    private String quantity;
    private BigDecimal orderBookDelta;

    private BigDecimal tradeBuyPrice;
    private BigDecimal tradeSellPrice;
    private BigDecimal orderBookAskPrice;
    private BigDecimal orderBookBidPrice;
}
