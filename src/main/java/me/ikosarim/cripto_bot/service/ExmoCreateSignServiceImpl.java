package me.ikosarim.cripto_bot.service;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Mac.getInstance;
import static org.apache.commons.codec.binary.Hex.encodeHexString;

@Service
public class ExmoCreateSignServiceImpl implements CreateSignService {

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
            System.out.println("Error creating sign in method - " + method);
            System.out.println("Error get instance in HmacSHA512");
            e.printStackTrace();
            return null;
        }

        try {
            mac.init(keySpec);
        } catch (InvalidKeyException e) {
            System.out.println("Error creating sign in method - " + method);
            System.out.println("Error init mac");
            e.printStackTrace();
            return null;
        }

        return encodeHexString(mac.doFinal(postData.toString().getBytes(UTF_8)));
    }
}