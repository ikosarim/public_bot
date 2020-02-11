package me.ikosarim.cripto_bot.validators;

import me.ikosarim.cripto_bot.containers.TradeObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import java.util.Set;

@Service
public class DataValidator implements Validator {

    @Autowired
    javax.validation.Validator validator;

    @Override
    public boolean supports(Class<?> aClass) {
        return TradeObject.class.equals(aClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        TradeObject tradeObject = (TradeObject) obj;
        Set<ConstraintViolation<Object>> validates = validator.validate(tradeObject);

        for (ConstraintViolation<Object> constraintViolation : validates) {
            String propertyPath = constraintViolation.getPropertyPath().toString();
            String message = constraintViolation.getMessage();
            errors.rejectValue(propertyPath, message);
        }
    }
}
