package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> filmStorage= new HashMap<>();
    private int numerator = 0;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        int id = ++numerator;
        film.setId(id);
        filmStorage.put(id, film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        int id = film.getId();
        if (!filmStorage.containsKey(id)) {
            String message = String.format("Фильм с id = %d не найден", id);
            throw new FilmNotFoundException(message);
        }
        filmStorage.put(id, film);
        log.info("Обновлен фильм {}", film);
        return film;
    }

    @GetMapping
    public Collection<Film> getAll() {
        return filmStorage.values();
    }
}