package me.ikosarim.cripto_bot.containers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ikosarim.cripto_bot.json_model.CurrencyPair;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CurrencyPairList {
    private List<CurrencyPair> pairList = new ArrayList<>();
}
