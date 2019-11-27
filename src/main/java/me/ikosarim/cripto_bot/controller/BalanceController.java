package me.ikosarim.cripto_bot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/balance")
public class BalanceController {



    @GetMapping
    public String getBalanceStatistics(Model model) {

        return "/balance";
    }
}