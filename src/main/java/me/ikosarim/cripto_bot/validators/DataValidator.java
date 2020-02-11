package me.ikosarim.cripto_bot.validators;

import me.ikosarim.cripto_bot.containers.CurrencyPairList;
import me.ikosarim.cripto_bot.containers.TradeObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Set;

@Service
public class DataValidator implements Validator {

    @Autowired
    javax.validation.Validator validator;

    @Override
    public boolean supports(Class<?> aClass) {
        return CurrencyPairList.class.equals(aClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        CurrencyPairList currencyPairList = (CurrencyPairList) obj;
        if (currencyPairList.getPairList().isEmpty()){
            errors.rejectValue("pairs", "is_empty");
        }

        Set<ConstraintViolation<Object>> validates = new HashSet<>() {{
            ((CurrencyPairList) obj).getPairList().forEach(
                    o -> addAll(validator.validate(o))
            );
        }};

        for (ConstraintViolation<Object> constraintViolation : validates) {
            String propertyPath = constraintViolation.getPropertyPath().toString();
            String message = constraintViolation.getMessage();
            errors.rejectValue(propertyPath, "", message);
        }
    }
}
