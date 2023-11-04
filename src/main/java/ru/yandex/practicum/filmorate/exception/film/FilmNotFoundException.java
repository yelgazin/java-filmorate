package ru.yandex.practicum.filmorate.exception.film;

import ru.yandex.practicum.filmorate.exception.BaseEntityNotFoundException;

public class FilmNotFoundException extends BaseEntityNotFoundException {
    public FilmNotFoundException(String message) {
        super(message);
    }

    public FilmNotFoundException(String message, Object... args) {
        super(message, args);
    }
}
