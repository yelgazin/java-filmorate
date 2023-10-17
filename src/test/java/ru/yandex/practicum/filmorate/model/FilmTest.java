package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest extends ValidatorTest {

    private Film film;

    @BeforeEach
    void beforeEach() {
        film = new Film();
        film.setId(1L);
        film.setName("Name");
        film.setDescription("Description");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.now());
    }

    @Test
    void setNullName_InvalidMessage() {
        film.setName(null);
        assertEquals("Название не может быть пустым",
                validateAndGetFirstMessageTemplate(film));
    }

    @Test
    void setEmptyName_InvalidMessage() {
        film.setName("");
        assertEquals("Название не может быть пустым",
                validateAndGetFirstMessageTemplate(film));
    }

    @Test
    void setDescription250Symbols_InvalidMessage() {
        String longDescription = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

        film.setDescription(longDescription);
        assertEquals("Максимальная длина описания — 200 символов",
                validateAndGetFirstMessageTemplate(film));
    }

    @Test
    void setInvalidReleaseDate_InvalidMessage() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate releaseDate = LocalDate.parse("1895-12-27", formatter);
        film.setReleaseDate(releaseDate);
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года",
                validateAndGetFirstMessageTemplate(film));
    }

    @Test
    void setZeroFilmDuration_InvalidMessage() {
        film.setDuration(0);
        assertEquals("Продолжительность фильма должна быть положительной",
                validateAndGetFirstMessageTemplate(film));
    }

    @Test
    void setCorrectProperties_NoErrors() {
        assertTrue(validator.validate(film).isEmpty());
    }
}