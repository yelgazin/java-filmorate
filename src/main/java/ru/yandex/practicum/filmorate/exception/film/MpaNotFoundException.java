package ru.yandex.practicum.filmorate.exception.film;

import ru.yandex.practicum.filmorate.exception.BaseEntityNotFoundException;

public class MpaNotFoundException extends BaseEntityNotFoundException {

    public MpaNotFoundException(String message) {
        super(message);
    }

    public MpaNotFoundException(String message, Object... args) {
        super(message, args);
    }
}
