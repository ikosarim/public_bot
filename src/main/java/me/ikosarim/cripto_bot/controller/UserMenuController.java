package me.ikosarim.cripto_bot.controller;

import me.ikosarim.cripto_bot.json_model.CurrencyPair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user_menu")
public class UserMenuController {

    @GetMapping
    public String getUserMenuPage(Model model) {
        List<CurrencyPair> currencyPairs = new ArrayList<>();
        model.addAttribute("currencyPairs", currencyPairs);
        return "/user_menu";
    }

    @PostMapping(value = "setCurrencyPairProperties", params = {"addPair"})
    public String addCurrencyPair(Model model, @ModelAttribute ArrayList<CurrencyPair> currencyPairs) {
        currencyPairs.add(new CurrencyPair());
        model.addAttribute("currencyPairs", currencyPairs);
        return "/user_menu";
    }
}
