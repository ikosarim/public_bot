package me.ikosarim.cripto_bot.containers;

import lombok.*;

import javax.validation.constraints.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TradeObject {

    @NotBlank(message = "Не введенено обозначение валютной пары")
    @Size(min = 2, max = 5, message = "Wrong length of message")
    private String pairName;

    @Positive(message = "Number negative or zero")
    private double sizeOfCorridor;
    @Positive(message = "Number negative or zero")
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