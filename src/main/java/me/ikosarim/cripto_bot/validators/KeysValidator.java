package me.ikosarim.cripto_bot.validators;

import me.ikosarim.cripto_bot.json_model.UserInfoEntity;
import me.ikosarim.cripto_bot.service.SendRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Map;

@Service
public class KeysValidator implements Validator {
    @Autowired
    SendRequestsService sendRequestsService;

    @Override
    public boolean supports(Class<?> aClass) {
        return Map.class.equals(aClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        UserInfoEntity userInfoEntity = sendRequestsService.sendCheckRequest((Map<String, String>) obj);
        if (userInfoEntity.getUid() == null) {
            errors.rejectValue("keys", "wrong.keys");
        }
    }
}
