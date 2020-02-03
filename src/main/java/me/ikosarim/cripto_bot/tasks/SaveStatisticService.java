package me.ikosarim.cripto_bot.tasks;

import me.ikosarim.cripto_bot.repos.AllStatisticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SaveStatisticService {

    @Autowired
    AllStatisticRepository allStatisticRepository;

    @Scheduled(cron = "0 0 12 * * ? 2020")
    public void saveStatistic() {

    }
}
