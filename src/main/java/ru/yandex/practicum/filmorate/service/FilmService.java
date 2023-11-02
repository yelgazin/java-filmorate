package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.film.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.film.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    @Qualifier("databaseFilmStorage")
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private Long numerator = 0L;

    public Film create(Film film) {
        validate(film);

        long id = ++numerator;
        film.setId(id);
        filmStorage.add(film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    public Film update(Film film) {
        validate(film);

        Long id = film.getId();
        Film savedFilm = filmStorage.findById(id)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id = %d не найден", id));

        savedFilm.setName(film.getName());
        savedFilm.setDescription(film.getDescription());
        savedFilm.setReleaseDate(film.getReleaseDate());
        savedFilm.setDuration(film.getDuration());
        savedFilm.setMpa(film.getMpa());
        savedFilm.setGenres(new TreeSet<>(film.getGenres()));
        filmStorage.update(savedFilm);
        log.info("Обновлен фильм {}", savedFilm);
        return savedFilm;
    }

    public Collection<Film> getAll() {
        return filmStorage.findAll();
    }

    public Film getById(Long filmId) {
        return filmStorage.findById(filmId)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id = %d не найден", filmId));
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id = %d не найден", filmId));

        if (userService.getById(userId) == null) {
            throw new UserNotFoundException("Пользователь с id = %d не найден", userId);
        }

        film.getLikes().add(userId);
        filmStorage.update(film);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id = %d не найден", filmId));

        if (userService.getById(userId) == null) {
            throw new UserNotFoundException("Пользователь с id = %d не найден", userId);
        }

        film.getLikes().remove(userId);
        filmStorage.update(film);
    }

    public Collection<Film> getTop(Integer count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Collection<Genre> getAllGenres() {
        return genreStorage.findAll();
    }

    public Genre getGenreById(Integer genreId) {
        return genreStorage.findById(genreId)
                .orElseThrow(() -> new GenreNotFoundException("Жанр с id = %d не найден", genreId));
    }

    public Collection<Mpa> getAllMpa() {
        return mpaStorage.findAll();
    }

    public Mpa getMpaById(Integer mpaId) {
        return mpaStorage.findById(mpaId)
                .orElseThrow(() -> new MpaNotFoundException("MPA с id = %d не найден", mpaId));
    }

    private void validate(Film film) {
        // Validate MPA
        Integer mpaId = film.getMpa().getId();
        mpaStorage.findById(film.getMpa().getId())
                .orElseThrow(() -> new MpaNotFoundException("MPA с id = %d не найден", mpaId));

        // Validate Genres
        List<Genre> allGenres = genreStorage.findAll();
        String invalidGenreIds = film.getGenres().stream()
                .filter(Predicate.not(allGenres::contains))
                .map(Genre::getId)
                .map(Object::toString)
                .collect(Collectors.joining(","));

        if (!invalidGenreIds.isBlank()) {
            throw new MpaNotFoundException("MPA с идентификаторами %s не найдены", invalidGenreIds);
        }
    }
}
