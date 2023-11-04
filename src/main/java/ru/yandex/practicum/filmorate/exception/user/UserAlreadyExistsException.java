package ru.yandex.practicum.filmorate.exception.user;

import ru.yandex.practicum.filmorate.exception.BaseEntityNotFoundException;

public class UserAlreadyExistsException extends BaseEntityNotFoundException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String message, Object... args) {
        super(message, args);
    }
}
