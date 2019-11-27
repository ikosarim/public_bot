package me.ikosarim.cripto_bot.controller;

import me.ikosarim.cripto_bot.json_model.UserInfoEntity;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/statistic")
public class BalanceController {

    @Autowired
    SendRequestsService sendRequestsService;

    @GetMapping
    public String getBalanceStatistics(Model model) {
        UserInfoEntity userInfoEntity = sendRequestsService.sendPostUserInfoRequest();
        model.addAttribute("userInfoEntity", userInfoEntity);
        return "/statistic";
    }

    @GetMapping(params = {"stop"})
    public String stopWork() {
        return "/user_menu";
    }
}