package ru.yandex.practicum.filmorate.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateAfterOrEqualValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DateAfterOrEqual {
    String message() default "Неверная дата";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String minDate();
}