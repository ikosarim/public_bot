package me.ikosarim.cripto_bot.controller;

import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.init.WorkTaskController;
import me.ikosarim.cripto_bot.tasks.ScalpingAlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Controller
@RequestMapping("/user_menu")
public class UserMenuController {

    @Autowired
    WorkTaskController workTaskController;

    @GetMapping
    public String getUserMenuPage(Model model) {
        ScheduledFuture<ScalpingAlgorithmTask> scalpingAlgorithmFuture = workTaskController.getScalpingAlgorithmFuture();
        if (scalpingAlgorithmFuture == null
                || scalpingAlgorithmFuture.isCancelled()) {
            model.addAttribute("currencyPairList", new CurrencyPairList());
            return "/user_menu";
        }
        return "redirect:/statistic";
    }

    @PostMapping(params = {"addPair"})
    public String addCurrencyPair(final CurrencyPairList pairList) {
        pairList.getPairList()
                .add(new TradeObject());
        return "/user_menu";
    }

    @PostMapping(params = {"removePair"})
    public String removeCurrencyPair(final CurrencyPairList pairList, final HttpServletRequest req) {
        final Integer pairId = Integer.valueOf(req.getParameter("removePair"));
        pairList.getPairList().remove(pairId.intValue());
        return "/user_menu";
    }

    @PostMapping(params = {"start"})
    public String startWork(@Valid @ModelAttribute CurrencyPairList currencyPairList, BindingResult bindingResult,
                            @RequestParam(value = "key") final String key,
                            @RequestParam(value = "secret") final String secret) {
        Map<String, String> keys = new HashMap<>() {{
            put("key", key);
            put("secret", secret);
        }};
        ObjectError error = workTaskController.validateUserKeys(keys);
        if (error != null) {
            bindingResult.addError(error);
        }
        if (bindingResult.hasErrors()) {
            return "/user_menu";
        }
        workTaskController.startTrade(currencyPairList);
        return "redirect:/statistic";
    }
}