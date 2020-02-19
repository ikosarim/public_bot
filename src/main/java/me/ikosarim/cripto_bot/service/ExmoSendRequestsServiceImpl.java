package me.ikosarim.cripto_bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;
import me.ikosarim.cripto_bot.json_model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.util.*;

import static java.util.Arrays.asList;

@Slf4j
@Service
@PropertySource("application.properties")
public class ExmoSendRequestsServiceImpl implements SendRequestsService {

    private Map<String, String> userPrivateInfoMap = new HashMap<>();

    @Autowired
    private JSonMappingService jSonMappingService;
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
    public Map<String, TradeObject> sendInitGetTradesRequest(String pairs, CurrencyPairList pairList) {
        return jSonMappingService.returnInitDataToTradeInMap(sendGetTradesRequestAndReturnNodeResult(pairs, 15), pairList);
    }

    @Override
    public Map<String, Double> sendGetTradesRequest(String pairUrl) {
        return jSonMappingService.returnDataToTradeInMap(sendGetTradesRequestAndReturnNodeResult(pairUrl, 1));
    }

    private JsonNode sendGetTradesRequestAndReturnNodeResult(String pairs, int i) {
        String uri = UriComponentsBuilder.fromUriString(env.getProperty("spring.http.url.trades"))
                .queryParam("limit", i)
                .queryParam("pair", pairs)
                .toUriString();
        return publicRestTemplate.getForObject(uri, JsonNode.class);
    }

    @Override
    public Map<String, PairSettingEntity> sendGetPairSettingsRequest() {
        String uri = UriComponentsBuilder.fromUriString(env.getProperty("spring.http.url.pair.settings"))
                .toUriString();
        return jSonMappingService.convertToPairSettingEntity(publicRestTemplate.getForObject(uri, JsonNode.class));
    }

    @Override
    public UserInfoEntity sendCheckRequest(Map<String, String> keysMap) {
        userPrivateInfoMap.putAll(keysMap);
        return sendPostUserInfoRequest();
    }

    @Override
    public UserInfoEntity sendPostUserInfoRequest() {
        String url = env.getProperty("spring.http.url.user.info");
        String method = url.substring(url.lastIndexOf("/") + 1);

        Map<String, Object> arguments = addNonceToRequest(null);

        MultiValueMap<String, Object> multiValueMapArguments = new LinkedMultiValueMap<>();
        arguments.forEach((key, value) -> multiValueMapArguments.put(key, new ArrayList<>() {{
            add(value);
        }}));

        HttpEntity requestEntity = new HttpEntity(multiValueMapArguments, createPostRequestHeaders(method, arguments));

        ResponseEntity<UserInfoEntity> response = privateRestTemplate.postForEntity(url, requestEntity, UserInfoEntity.class);

        return response.getBody();
    }

    @Override
    public Map<String, List<OpenOrderEntity>> sendGetOpenOrders() {
        String url = env.getProperty("spring.http.url.open.orders");
        String method = url.substring(url.lastIndexOf("/") + 1);

        Map<String, Object> arguments = addNonceToRequest(null);

        MultiValueMap<String, Object> multiValueMapArguments = new LinkedMultiValueMap<>();
        arguments.forEach((key, value) -> multiValueMapArguments.put(key, new ArrayList<>() {{
            add(value);
        }}));

        HttpEntity requestEntity = new HttpEntity(multiValueMapArguments, createPostRequestHeaders(method, arguments));

        ResponseEntity<JsonNode> response = privateRestTemplate.postForEntity(url, requestEntity, JsonNode.class);

        return jSonMappingService.mapToOpenOrdersEntity(response.getBody());
    }

    @Override
    public OrderBookEntity sendGetOrderBookRequest(String pair) {
        String uri = UriComponentsBuilder.fromUriString(env.getProperty("spring.http.order.book"))
                .queryParam("pair", pair)
                .queryParam("limit", 1)
                .toUriString();
        return publicRestTemplate.getForObject(uri, OrderBookEntity.class);
    }

    @Override
    public OrderCancelStatus sendOrderCancelRequest(Map<String, Object> args) {
        String url = env.getProperty("spring.http.url.order.cancel");
        String method = url.substring(url.lastIndexOf("/") + 1);

        Map<String, Object> arguments = addNonceToRequest(args);

        MultiValueMap<String, Object> multiValueMapArguments = new LinkedMultiValueMap<>();
        arguments.forEach((key, value) -> multiValueMapArguments.put(key, new ArrayList<>() {{
            add(value);
        }}));

        HttpEntity requestEntity = new HttpEntity(multiValueMapArguments, createPostRequestHeaders(method, arguments));

        ResponseEntity<OrderCancelStatus> response = privateRestTemplate.postForEntity(url, requestEntity, OrderCancelStatus.class);

        return response.getBody();
    }

    @Override
    public OrderCreateStatus sendOrderCreateRequest(Map<String, Object> args) {
        String url = env.getProperty("spring.http.url.order.create");
        String method = url.substring(url.lastIndexOf("/") + 1);

        Map<String, Object> arguments = addNonceToRequest(args);

        MultiValueMap<String, Object> multiValueMapArguments = new LinkedMultiValueMap<>();
        arguments.forEach((key, value) -> multiValueMapArguments.put(key, new ArrayList<>() {{
            add(value);
        }}));

        HttpEntity requestEntity = new HttpEntity(multiValueMapArguments, createPostRequestHeaders(method, arguments));

        ResponseEntity<OrderCreateStatus> response;
        try {
            response = privateRestTemplate.postForEntity(url, requestEntity, OrderCreateStatus.class);
        } catch (RestClientException e) {
            log.error("Exception in send create order request or in mapping to OrderCreateStatus");
            log.error(Arrays.toString(e.getStackTrace()));
            return OrderCreateStatus.builder()
                    .orderId(null)
                    .error(e.getMessage())
                    .result(false)
                    .build();
        }

        return response.getBody();
    }

    @Override
    public List<UserTradeEntity> sendGetTradeResult(Map<String, Object> tradeResultArguments) {
        String url = env.getProperty("spring.http.url.trades.result");
        String method = url.substring(url.lastIndexOf("/") + 1);

        Map<String, Object> arguments = addNonceToRequest(tradeResultArguments);

        MultiValueMap<String, Object> multiValueMapArguments = new LinkedMultiValueMap<>();
        arguments.forEach((key, value) -> multiValueMapArguments.put(key, new ArrayList<>() {{
            add(value);
        }}));

        HttpEntity requestEntity = new HttpEntity(multiValueMapArguments, createPostRequestHeaders(method, arguments));

        ResponseEntity<UserTradeEntity> response = privateRestTemplate.postForEntity(url, requestEntity, UserTradeEntity.class);

        return asList(response.getBody());
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
        httpHeaders.add("Key", userPrivateInfoMap.get("key"));
        String sign = createSignService.createSign(method, userPrivateInfoMap.get("secret"), arguments);
        httpHeaders.add("Sign", sign);
        return httpHeaders;
    }
}