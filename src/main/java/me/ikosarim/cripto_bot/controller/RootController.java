package me.ikosarim.cripto_bot.controller;

import me.ikosarim.cripto_bot.init.WorkTaskController;
import me.ikosarim.cripto_bot.tasks.ScalpingAlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.ScheduledFuture;

@Controller
@RequestMapping("/")
public class RootController {

    @Autowired
    private WorkTaskController workTaskController;

    @GetMapping
    private String redirectFromRoot() {
        ScheduledFuture<ScalpingAlgorithmTask> scalpingAlgorithmFuture = workTaskController.getScalpingAlgorithmFuture();
        if (scalpingAlgorithmFuture == null
                || scalpingAlgorithmFuture.isCancelled()) {
            return "redirect:/user_menu";
        }
        return "redirect:/statistic";
    }
}
