package servicesTests;

import me.ikosarim.cripto_bot.service.SendRequestsServiceImpl;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(SendRequestsServiceTests.SendRequestsServiceTestsConfig.class)
public class SendRequestsServiceTests {

    @TestConfiguration()
    static class SendRequestsServiceTestsConfig {

        @Bean(name = "testRequestService")
        SendRequestsService sendRequestsService() {
            return new SendRequestsServiceImpl();
        }

        @Bean
        RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }

    @Resource(name = "testRequestService")
    SendRequestsService sendRequestsService;

    @Test
    public void test() {
//        sendRequestsService.sendGetTradesRequest("BTC_USD,ETH_USD");
    }
}
