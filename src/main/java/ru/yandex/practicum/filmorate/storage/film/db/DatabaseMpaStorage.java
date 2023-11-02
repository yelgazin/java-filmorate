package ru.yandex.practicum.filmorate.storage.film.db;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DatabaseMpaStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(Mpa mpa) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Mpa mpa) {
        throw new NotImplementedException();
    }

    @Override
    public boolean contains(Mpa mpa) {
        return findById(mpa.getId()).isPresent();
    }

    @Override
    public void remove(Mpa mpa) {
        throw new NotImplementedException();
    }

    @Override
    public void removeById(Integer id) {
        throw new NotImplementedException();
    }

    @Override
    public Optional<Mpa> findById(Integer id) {
        return jdbcTemplate.query("SELECT mpa_id, name, description FROM mpa WHERE mpa_id = ?",
                this::mapMpa,
                id).stream().findAny();
    }

    @Override
    public List<Mpa> findAll() {
        return jdbcTemplate.query("SELECT mpa_id, name, description FROM mpa", this::mapMpa);
    }

    private Mpa mapMpa(ResultSet resultSet, int rowNum) throws SQLException {
        Integer id = resultSet.getInt("mpa_id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");

        return Mpa.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();
    }
}