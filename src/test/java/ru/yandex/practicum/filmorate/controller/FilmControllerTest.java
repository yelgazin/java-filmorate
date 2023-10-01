package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.exception.RestException;
import ru.yandex.practicum.filmorate.exception.RestExceptionHandler;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FilmControllerTest extends AbstractControllerTest {

    private Film film;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new FilmController())
                .setControllerAdvice(new RestExceptionHandler())
                .build();

        film = new Film();
        film.setName("Some name");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        film.setDescription("Some description");
    }

    @Test
    public void createFilmWithNullName_ResponseBadRequest() throws Exception {
        film.setName(null);
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/films", film))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Название не может быть пустым", ex.getMessage());
    }

    @Test
    public void createFilmWithEmptyName_ResponseBadRequest() throws Exception {
        film.setName("");
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/films", film))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Название не может быть пустым", ex.getMessage());
    }

    @Test
    public void createFilmWithDescriptionOf201Symbols_ResponseBadRequest() throws Exception {
        String stringOf201Symbols = IntStream.range(1, 202).mapToObj(i -> "#").collect(Collectors.joining());
        film.setDescription(stringOf201Symbols);
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/films", film))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Максимальная длина описания — 200 символов", ex.getMessage());
    }

    @Test
    public void createFilmWithDescriptionOf200Symbols_CreatedFilm() throws Exception {
        String stringOf201Symbols = IntStream.range(1, 201).mapToObj(i -> "#").collect(Collectors.joining());
        film.setDescription(stringOf201Symbols);
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/films", film))
                .andExpect(status().isOk())
                .andReturn();
        Film ex = fromResult(result, Film.class);
        assertEquals(stringOf201Symbols, film.getDescription());
    }

    @Test
    public void createFilmWithInvalidReleaseDate_ResponseBadRequest() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate releaseDate = LocalDate.parse("1895-12-27");
        film.setReleaseDate(releaseDate);
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/films", film))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", ex.getMessage());
    }

    @Test
    public void createFilmWithValidReleaseDate_CreatedFilm() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate releaseDate = LocalDate.parse("1895-12-28");
        film.setReleaseDate(releaseDate);
        mockMvc.perform(getPostRequestBuilder("/films", film))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void createFilmWithNonPositiveDuration_ResponseBadRequest() throws Exception {
        film.setDuration(0);
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/films", film))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Продолжительность фильма должна быть положительной", ex.getMessage());
    }

    @Test
    public void createFilm_CreatedFilm() throws Exception {
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/films", film))
                .andExpect(status().isOk())
                .andReturn();

        Film createdFilm = fromResult(result, Film.class);
        film.setId(1);
        assertEquals(film, createdFilm);
    }

    @Test
    public void updateFilmWithInvalidId_ResponseNotFound() throws Exception {
        film.setId(1000);
        MvcResult result = mockMvc.perform(getPutRequestBuilder("/films", film))
                .andExpect(status().isNotFound())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals(String.format("Фильм с id = %d не найден", film.getId()), ex.getMessage());
    }

    @Test
    public void updateFilm_UpdatedFilm() throws Exception {
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/films", film))
                .andExpect(status().isOk())
                .andReturn();

        Film createdFilm = fromResult(result, Film.class);
        createdFilm.setName("New name");
        createdFilm.setDescription("New description");
        createdFilm.setDuration(240);
        createdFilm.setReleaseDate(LocalDate.now().minusYears(5));

        result = mockMvc.perform(getPutRequestBuilder("/films", createdFilm))
                .andExpect(status().isOk())
                .andReturn();

        Film updatedFilm= fromResult(result, Film.class);
        assertEquals(createdFilm, updatedFilm);
    }

    @Test
    public void getAllFilms_ReturnerList() throws Exception {
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/films", film))
                .andExpect(status().isOk())
                .andReturn();

        film.setName("Another film");
        result = mockMvc.perform(getPostRequestBuilder("/films", film))
                .andExpect(status().isOk())
                .andReturn();

        result = mockMvc.perform(getGetRequestBuilder("/films"))
                .andExpect(status().isOk())
                .andReturn();

        List<Film> films = fromResult(result,  new TypeReference<List<Film>>() {});
        assertEquals(2, films.size());
    }
}

