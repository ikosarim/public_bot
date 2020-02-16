package me.ikosarim.cripto_bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Mac.getInstance;
import static org.apache.commons.codec.binary.Hex.encodeHexString;

@Service
public class ExmoCreateSignServiceImpl implements CreateSignService {

    Logger logger = LoggerFactory.getLogger(ExmoCreateSignServiceImpl.class);

    @Override
    public String createSign(String method, String secret, Map<String, Object> arguments) {

        StringBuilder postData = new StringBuilder();

        for (Map.Entry<String, Object> stringStringEntry : arguments.entrySet()) {

            if (postData.length() > 0) {
                postData.append("&");
            }
            postData.append(((Map.Entry) stringStringEntry).getKey())
                    .append("=")
                    .append(stringStringEntry.getValue().toString());
        }

        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(UTF_8), "HmacSHA512");

        Mac mac;
        try {
            mac = getInstance("HmacSHA512");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error creating sign in method - " + method);
            logger.error("Error get instance in HmacSHA512");
            logger.error(Arrays.toString(e.getStackTrace()));
            return null;
        }

        try {
            mac.init(keySpec);
        } catch (InvalidKeyException e) {
            logger.error("Error creating sign in method - " + method);
            logger.error("Error init mac");
            logger.error(Arrays.toString(e.getStackTrace()));
            return null;
        }

        return encodeHexString(mac.doFinal(postData.toString().getBytes(UTF_8)));
    }
}