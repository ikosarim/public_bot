package me.ikosarim.cripto_bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@PropertySource("application.yml")
public class Requests {

    private Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    private JsonNode sendPublicGetTradesRequest(String pairs) {
        String uri = UriComponentsBuilder.fromUriString(env.getProperty("spring.http.url.trades"))
                .queryParam("limit", 15)
                .queryParam("pair", pairs)
                .toUriString();
        return null;
    }

    private JsonNode sendPrivatePostRequest() {
        return null;
    }
}
