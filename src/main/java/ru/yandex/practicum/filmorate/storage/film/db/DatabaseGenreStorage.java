package ru.yandex.practicum.filmorate.storage.film.db;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DatabaseGenreStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(Genre genre) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Genre genre) {
        throw new NotImplementedException();
    }

    @Override
    public boolean contains(Genre genre) {
        return findById(genre.getId()).isPresent();
    }

    @Override
    public void remove(Genre genre) {
        throw new NotImplementedException();
    }

    @Override
    public void removeById(Integer id) {
        throw new NotImplementedException();
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        return jdbcTemplate.query("SELECT genre_id, name FROM genre WHERE genre_id = ?",
                this::mapGenre,
                id).stream().findAny();
    }

    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query("SELECT genre_id, name FROM genre", this::mapGenre);
    }

    private Genre mapGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Integer id = resultSet.getInt("genre_id");
        String name = resultSet.getString("name");

        return Genre.builder()
                .id(id)
                .name(name)
                .build();
    }
}