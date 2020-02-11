package me.ikosarim.cripto_bot.containers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CurrencyPairList {
    @NotEmpty(message = "Не введено ни одной валютной пары")
    private List<TradeObject> pairList = new ArrayList<>();
}