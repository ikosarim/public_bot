package me.ikosarim.cripto_bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;

@Service
@PropertySource("application.properties")
public class ExmoSendRequestsServiceImpl implements SendRequestsService {

    private Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    @Resource(name = "publicRestTemplate")
    RestTemplate publicRestTemplate;
    @Resource(name = "privateRestTemplate")
    RestTemplate privateRestTemplate;

    @Override
    public JsonNode sendGetTradesRequest(String pairs) {
        String uri = UriComponentsBuilder.fromUriString(env.getProperty("spring.http.url.trades"))
                .queryParam("limit", 15)
                .queryParam("pair", pairs)
                .toUriString();
        return publicRestTemplate.getForObject(uri, JsonNode.class);
    }

    @Override
    public JsonNode sendGetPairSettingsRequest() {
        String uri = UriComponentsBuilder.fromUriString(env.getProperty("spring.http.url.pair.settings"))
                .toUriString();
        return publicRestTemplate.getForObject(uri, JsonNode.class);
    }

    @Override
    public JsonNode sendPostUserInfoRequest() {
        // TODO: 27.11.2019 generate sign and add to request headers
        String uri = UriComponentsBuilder.fromUriString(env.getProperty("spring.http.url.user.info"))
                .toUriString();

        return null;
    }
}