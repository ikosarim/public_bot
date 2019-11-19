import me.ikosarim.cripto_bot.Application;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
public class SendRequestsServiceTests {

    @Autowired
    SendRequestsService sendRequestsService;

    @Test
    public void test() {
        sendRequestsService.sendGetTradesRequest("BTC_USD,ETH_USD");
    }
}
