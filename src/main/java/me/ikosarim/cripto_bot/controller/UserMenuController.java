package me.ikosarim.cripto_bot.controller;

import lombok.extern.slf4j.Slf4j;
import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user_menu")
@Slf4j
public class UserMenuController {

    @Autowired
    Map<String, String> userPrivateInfoMap;

    @Resource(name = "dataValidator")
    private Validator dataValidator;

    @Resource(name = "keysValidator")
    private Validator keysValidator;

    @GetMapping
    public String getUserMenuPage(Model model) {
        model.addAttribute("currencyPairList", new CurrencyPairList());
        return "/user_menu";
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
//        bindingResult.
        currencyPairList.getPairList().forEach(
                to -> dataValidator.validate(to, bindingResult)
        );
        if (bindingResult.hasErrors()) {
            return "/user_menu";
        }
//        if (currencyPairList.getPairList().isEmpty()) {
//            log.warn("Не выбраны валютные пары"); // logging
//            return "redirect:/user_menu";
//        }
        Map<String, String> keys = new HashMap<>() {{
            put("key", key);
            put("secret", secret);
        }};
        keysValidator.validate(keys, bindingResult);
//        initUserPrivateInfoMap(key, secret);
        log.debug("Дергаем метод логики работы приложения");
        log.debug("Возможно добавляем редирект на страницу отображения или рисуем какой-нибудь картинку работы... или нет");
        return "redirect:/statistic";
    }

    private void initUserPrivateInfoMap(String key, String secret) {
        userPrivateInfoMap.put("key", key);
        userPrivateInfoMap.put("secret", secret);
    }

    // TODO: 19.11.2019 Добавить валидации

    // TODO: 12.02.2020 https://habr.com/ru/post/424819/ 
}