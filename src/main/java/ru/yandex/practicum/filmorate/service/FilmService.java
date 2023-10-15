package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private Long numerator = 0L;

    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film create(Film film) {
        long id = ++numerator;
        film.setId(id);
        filmStorage.add(film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    public Film update(Film film) {
        Long id = film.getId();
        Film savedFilm = filmStorage.findById(id);

        if (savedFilm == null) {
            String message = String.format("Фильм с id = %d не найден", id);
            throw new FilmNotFoundException(message);
        }

        savedFilm.setName(film.getName());
        savedFilm.setDescription(film.getDescription());
        savedFilm.setReleaseDate(film.getReleaseDate());
        savedFilm.setDuration(film.getDuration());
        filmStorage.update(savedFilm);
        log.info("Обновлен фильм {}", film);
        return film;
    }

    public Collection<Film> getAll() {
        return filmStorage.findAll();
    }

    public Film getById(Long filmId) {
        Film film = filmStorage.findById(filmId);
        if (film == null) {
            String message = String.format("Фильм с id = %d не найден", filmId);
            throw new FilmNotFoundException(message);
        }

        return film;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId);

        if (film == null) {
            String message = String.format("Фильм с id = %d не найден", filmId);
            throw new FilmNotFoundException(message);
        }
        if (userService.getById(userId) == null) {
            String message = String.format("Пользователь с id = %d не найден", userId);
            throw new UserNotFoundException(message);
        }

        film.getLikes().add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId);

        if (film == null) {
            String message = String.format("Фильм с id = %d не найден", filmId);
            throw new FilmNotFoundException(message);
        }
        if (userService.getById(userId) == null) {
            String message = String.format("Пользователь с id = %d не найден", userId);
            throw new UserNotFoundException(message);
        }

        film.getLikes().remove(userId);
    }

    public Collection<Film> getTop(Integer count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
