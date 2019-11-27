package me.ikosarim.cripto_bot.service;

import java.util.Map;

public interface CreateSignService {

    String createSign(String method, String secret, Map<String, Object> arguments);
}