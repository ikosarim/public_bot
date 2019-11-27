package me.ikosarim.cripto_bot.containers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CurrencyPairList {
    private List<TradeObject> pairList = new ArrayList<>();
}