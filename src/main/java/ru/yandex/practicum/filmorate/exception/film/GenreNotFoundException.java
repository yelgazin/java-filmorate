package ru.yandex.practicum.filmorate.exception.film;

import ru.yandex.practicum.filmorate.exception.BaseEntityNotFoundException;

public class GenreNotFoundException extends BaseEntityNotFoundException {
    public GenreNotFoundException(String message) {
        super(message);
    }

    public GenreNotFoundException(String message, Object... args) {
        super(message, args);
    }
}
