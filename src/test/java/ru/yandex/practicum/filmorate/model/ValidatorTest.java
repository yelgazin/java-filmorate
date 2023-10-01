package ru.yandex.practicum.filmorate.model;

import javax.validation.Validation;
import javax.validation.Validator;

public abstract class ValidatorTest {
    protected static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    protected  <T> String validateAndGetFirstMessageTemplate(T obj) {
        return validator.validate(obj).stream()
                .findFirst()
                .get()
                .getConstraintDescriptor()
                .getMessageTemplate();
    }
}
