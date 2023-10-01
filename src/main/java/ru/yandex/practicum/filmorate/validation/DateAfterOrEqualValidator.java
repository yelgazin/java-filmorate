package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateAfterOrEqualValidator implements ConstraintValidator<DateAfterOrEqual, LocalDate> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateAfterOrEqual constraintAnnotation;
    @Override
    public void initialize(DateAfterOrEqual constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(LocalDate dateField, ConstraintValidatorContext cxt) {
        if (dateField == null) {
            return false;
        }

        try {
            LocalDate minDate = LocalDate.parse(constraintAnnotation.minDate(), formatter);
            return !dateField.isBefore(minDate);
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

}