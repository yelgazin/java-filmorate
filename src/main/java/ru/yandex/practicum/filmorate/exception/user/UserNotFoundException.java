package ru.yandex.practicum.filmorate.exception.user;

import ru.yandex.practicum.filmorate.exception.BaseEntityNotFoundException;

public class UserNotFoundException extends BaseEntityNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Object... args) {
        super(message, args);
    }
}
