package ru.yandex.practicum.filmorate.exception;

public abstract class BaseEntityNotFoundException extends  RuntimeException {
    public BaseEntityNotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }

    public BaseEntityNotFoundException(String message) {
        super(message);
    }
}
