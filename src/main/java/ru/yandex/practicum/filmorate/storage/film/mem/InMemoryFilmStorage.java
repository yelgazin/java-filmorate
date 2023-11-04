package ru.yandex.practicum.filmorate.storage.film.mem;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.BaseInMemoryStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@Component
public class InMemoryFilmStorage extends BaseInMemoryStorage<Film, Long> implements FilmStorage {
}
