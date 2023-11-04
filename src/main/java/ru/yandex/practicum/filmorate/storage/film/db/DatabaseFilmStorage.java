package ru.yandex.practicum.filmorate.storage.film.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("databaseFilmStorage")
@RequiredArgsConstructor
public class DatabaseFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DatabaseGenreStorage databaseGenreStorage;
    private final DatabaseMpaStorage databaseMpaStorage;

    @Transactional
    @Override
    public void add(Film film) {
        String query = "INSERT INTO film (film_id, name, description, mpa_id, release_date, duration) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(query,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getMpa().getId(),
                film.getReleaseDate(),
                film.getDuration());

        addGenre(film, film.getGenres());
    }

    @Transactional
    @Override
    public void update(Film entity) {
        String query = "UPDATE film " +
                "SET name = ?," +
                "    description = ?," +
                "    mpa_id = ?," +
                "    release_date = ?," +
                "    duration = ?" +
                "WHERE film_id = ?";

        jdbcTemplate.update(query,
                entity.getName(),
                entity.getDescription(),
                entity.getMpa().getId(),
                entity.getReleaseDate(),
                entity.getDuration(),
                entity.getId());

        // Update Genres
        Set<Integer> newGenreIds = entity.getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        List<Integer> existGenreIds = findGenresIdsByFilmId(entity.getId());

        List<Integer> genreIdsToAdd = new ArrayList<>(newGenreIds);
        genreIdsToAdd.removeAll(existGenreIds);
        addGenres(entity, genreIdsToAdd);

        List<Integer> genreIdsToRemove = new ArrayList<>(existGenreIds);
        genreIdsToRemove.removeAll(newGenreIds);
        removeGenres(entity, genreIdsToRemove);

        // Update likes
        Set<Long> newUserIds = entity.getLikes();
        List<Long> existUserIds = findLikesOfUsersByFilmId(entity.getId());
        List<Long> userIdsToAdd = new ArrayList<>(newUserIds);
        userIdsToAdd.remove(existUserIds);
        addLikes(entity, userIdsToAdd);

        List<Long> likesToRemove = new ArrayList<>(existUserIds);
        existUserIds.remove(newUserIds);
        removeLikes(entity, likesToRemove);
    }

    @Override
    public boolean contains(Film film) {
        return findById(film.getId()).isPresent();
    }

    @Override
    public void remove(Film film) {
        removeById(film.getId());
    }

    @Transactional
    @Override
    public void removeById(Long id) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM film WHERE film_id = ?", id);
    }

    @Override
    public Optional<Film> findById(Long id) {
        return jdbcTemplate.query(
                "SELECT film_id, name, description, mpa_id, release_date, duration FROM film WHERE film_id = ?",
                this::mapFilm, id).stream().findAny();
    }

    @Override
    public List<Film> findAll() {
        return jdbcTemplate.query(
                "SELECT film_id, name, description, mpa_id, release_date, duration FROM film",
                this::mapFilm);
    }

    private Film mapFilm(ResultSet rs, int rowNum) throws SQLException {
        Long filmId = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        Integer mpaId = rs.getInt("mpa_id");
        Mpa mpa = databaseMpaStorage.findById(mpaId).orElse(null);
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Integer duration = rs.getInt("duration");

        Film film = Film.builder()
                .id(filmId)
                .name(name)
                .description(description)
                .mpa(mpa)
                .duration(duration)
                .releaseDate(releaseDate)
                .build();

        film.setGenres(findGenresByFilmId(film.getId()));
        film.setLikes(new HashSet<>(findLikesOfUsersByFilmId(film.getId())));
        return film;
    }

    private void addGenre(Film film, Set<Genre> genres) {
        String query = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        genres.forEach(genre -> jdbcTemplate.update(query, film.getId(), genre.getId()));
    }

    private void addGenres(Film film, List<Integer> genreIds) {
        String query = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        genreIds.forEach(genreId -> jdbcTemplate.update(query, film.getId(), genreId));
    }

    private void removeGenres(Film film, List<Integer> genreIds) {
        String query = "DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?";
        genreIds.forEach(genreId -> jdbcTemplate.update(query, film.getId(), genreId));
    }

    private List<Integer> findGenresIdsByFilmId(Long filmId) {
        return jdbcTemplate.query("SELECT genre_id FROM film_genre WHERE film_id = ?",
                (rs, rowNum) -> rs.getInt("genre_id"),
                filmId);
    }

    private Set<Genre> findGenresByFilmId(Long filmId) {
        return findGenresIdsByFilmId(filmId).stream()
                .map(databaseGenreStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private List<Long> findLikesOfUsersByFilmId(Long filmId) {
        return jdbcTemplate.query("SELECT user_id FROM film_like WHERE film_id = ?",
                (rs, rowNum) -> rs.getLong("user_id"),
                filmId);
    }

    private void addLikes(Film film, List<Long> userIds) {
        String query = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";
        userIds.forEach(userId -> jdbcTemplate.update(query, film.getId(), userId));
    }

    private void removeLikes(Film film, List<Long> userIds) {
        String query = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";
        userIds.forEach(userId -> jdbcTemplate.update(query, film.getId(), userId));
    }
}


