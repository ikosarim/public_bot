package me.ikosarim.cripto_bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import me.ikosarim.cripto_bot.json_model.UserInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
@PropertySource("application.properties")
public class ExmoSendRequestsServiceImpl implements SendRequestsService {

    @Autowired
    Map<String, String> userPrivateInfoMap;

    @Autowired
    CreateSignService createSignService;

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
    public UserInfoEntity sendPostUserInfoRequest() {
        String url = env.getProperty("spring.http.url.user.info");
        String method = url.substring(url.lastIndexOf("/") + 1);

        Map<String, Object> arguments = addNonceToRequest(null);
        HttpEntity requestEntity = new HttpEntity(arguments, createPostRequestHeaders(method, arguments));

        ResponseEntity<UserInfoEntity> response = privateRestTemplate.postForEntity(url, requestEntity, UserInfoEntity.class);

        return response.getBody();
    }

    private Map<String, Object> addNonceToRequest(Map<String, Object> arguments) {
        if (arguments == null) {
            arguments = new HashMap<>();
        }

        String nonceNum = "" + System.nanoTime();
        String nonceName = "nonce";
        arguments.put(nonceName, nonceNum);

        return arguments;
    }

    private HttpHeaders createPostRequestHeaders(String method, Map<String, Object> arguments) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        httpHeaders.add("Content-type", "application/x-www-form-urlencoded");
        httpHeaders.add("Key", userPrivateInfoMap.get("key"));
        String sign = createSignService.createSign(method, userPrivateInfoMap.get("secret"), arguments);
        httpHeaders.add("Sign", sign);
        return httpHeaders;
    }
}