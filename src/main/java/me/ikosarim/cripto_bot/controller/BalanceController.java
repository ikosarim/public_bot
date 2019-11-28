package me.ikosarim.cripto_bot.controller;

import me.ikosarim.cripto_bot.json_model.UserInfoEntity;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Controller
@RequestMapping("/statistic")
public class BalanceController {

    @Autowired
    SendRequestsService sendRequestsService;

    @GetMapping
    public String getBalanceStatistics(Model model) {
        UserInfoEntity userInfoEntity = sendRequestsService.sendPostUserInfoRequest();
        model.addAttribute("userInfoEntity", clearStatistic(userInfoEntity));
        return "/statistic";
    }

    @GetMapping(params = {"stop"})
    public String stopWork() {
        return "/user_menu";
    }

    private UserInfoEntity clearStatistic(UserInfoEntity userInfoEntity) {
        Map<String, String> balanceMap = userInfoEntity.getBalances();
        balanceMap = balanceMap.entrySet()
                .stream()
                .filter(e -> !"0".equals(e.getValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, String> reserveMap = userInfoEntity.getReserved();
        reserveMap = reserveMap.entrySet()
                .stream()
                .filter(e -> !"0".equals(e.getValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        userInfoEntity.setBalances(balanceMap);
        userInfoEntity.setReserved(reserveMap);

        return userInfoEntity;
    }
}