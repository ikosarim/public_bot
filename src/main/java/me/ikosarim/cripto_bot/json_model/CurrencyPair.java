package me.ikosarim.cripto_bot.json_model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CurrencyPair {

    String pairName;
    String qty;
    Integer maxOrdersCount;
    BigDecimal percent;
}
