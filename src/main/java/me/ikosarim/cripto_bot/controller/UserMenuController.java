package me.ikosarim.cripto_bot.controller;

import lombok.extern.slf4j.Slf4j;
import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.init.InitFirstTradeObjects;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user_menu")
@Slf4j
public class UserMenuController {

    @Autowired
    SendRequestsService sendRequestsService;
    @Autowired
    InitFirstTradeObjects initFirstTradeObjects;

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
    public String startWork(@ModelAttribute CurrencyPairList currencyPairList,
                            @RequestParam(value = "key") final String key,
                            @RequestParam(value = "secret") final String secret) {
        if (currencyPairList.getPairList().isEmpty()) {
            log.warn("Не выбраны валютные пары");
            return "redirect:/user_menu";
        }
        initFirstTradeObjects.initTradeObjectMap(currencyPairList);
        log.debug("Дергаем метод логики работы приложения");
        log.debug("Возможно добавляем редирект на страницу отображения или рисуем какой-нибудь картинку работы... или нет");
        return "/user_menu";
    }

    @PostMapping(params = {"stop"})
    public String stopWork() {
        log.warn("Проверяем, что приложение запущено иначе просто редиректим");
        log.warn("Может скрываем форму работы приложения");
        return "/user_menu";
    }

    @PostMapping(params = {"sendReq"})
    public String sendReq() {
        sendRequestsService.sendGetTradesRequest("BTC_USD,ETH_USD");
        return "/user_menu";
    }

    // TODO: 19.11.2019 Добавить валидации
}