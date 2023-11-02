package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.exception.RestException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.model.user.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmControllerTest extends AbstractControllerTest {

    private Film film;
    private User user;

    @BeforeEach
    public void setup() {
        film = Film.builder()
                .name("Some name")
                .releaseDate(LocalDate.now())
                .duration(120)
                .description("Some description")
                .mpa(Mpa.builder().id(1).build())
                .genres(Set.of(Genre.builder().id(1).build()))
                .build();

        user = new User();
        user.setEmail("valid@mail.ru");
        user.setLogin("loginName");
        user.setName("Some name");
        user.setBirthday(LocalDate.now().minusYears(40));
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
        film.setId(1L);
        assertEquals(film, createdFilm);
    }

    @Test
    public void updateFilmWithInvalidId_ResponseNotFound() throws Exception {
        film.setId(1000L);
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

        Film updatedFilm = fromResult(result, Film.class);
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

        List<Film> films = fromResult(result, new TypeReference<List<Film>>() {
        });
        assertEquals(2, films.size());
    }

    @Test
    public void getFilmById_ReturnedFilm() throws Exception {
        createFilms(2);

        MvcResult result = mockMvc.perform(getGetRequestBuilder("/films/2"))
                .andExpect(status().isOk())
                .andReturn();

        Film requestedFilm = fromResult(result, Film.class);
        film.setId(2L);
        assertEquals(film, requestedFilm);
    }

    @Test
    public void getFilmByIdWithIncorrectId_ResponseNotFound() throws Exception {
        createFilms(1);
        MvcResult result = mockMvc.perform(getGetRequestBuilder("/films/999"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void addLike_ResponseOk() throws Exception {
        createUsers(1);
        createFilms(1);
        mockMvc.perform(getPutRequestBuilder("/films/1/like/1", ""))
                .andExpect(status().isOk());
    }

    @Test
    public void addLikeInvalidUserId_ResponseNotFound() throws Exception {
        createFilms(1);
        mockMvc.perform(getPutRequestBuilder("/films/1/like/999", ""))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addLikeTwice_ResponseOk() throws Exception {
        createFilms(1);
        mockMvc.perform(getPutRequestBuilder("/films/1/like/1", ""))
                .andExpect(status().isNotFound());
        mockMvc.perform(getPutRequestBuilder("/films/1/like/1", ""))
                .andExpect(status().isNotFound());
    }

    @Test
    public void removeLike_ResponseOk() throws Exception {
        createFilms(1);
        createUsers(1);
        mockMvc.perform(getPutRequestBuilder("/films/1/like/1", ""))
                .andExpect(status().isOk());
        mockMvc.perform(getDeleteRequestBuilder("/films/1/like/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void removeLikeNonExistentUser_ResponseNotFound() throws Exception {
        createFilms(1);
        mockMvc.perform(getDeleteRequestBuilder("/films/1/like/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void removeLikeNonExistentFilm_ResponseNotFound() throws Exception {
        createUsers(1);
        mockMvc.perform(getDeleteRequestBuilder("/films/999/like/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getTopFilmsWithoutCountLimit_TenSortedFilms() throws Exception {
        int defaultLimit = 10;
        createFilms(20);
        createUsers(1);

        mockMvc.perform(getPutRequestBuilder("/films/20/like/1", ""))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(getGetRequestBuilder("/films/popular"))
                .andExpect(status().isOk())
                .andReturn();
        List<Film> films = fromResult(result, new TypeReference<List<Film>>() {
        });
        assertEquals(defaultLimit, films.size());
        assertEquals(20, films.get(0).getId());
    }

    @Test
    public void getTopFilmsWithoutCountLimitFive_FiveSortedFilms() throws Exception {
        createFilms(20);
        createUsers(1);

        mockMvc.perform(getPutRequestBuilder("/films/20/like/1", ""))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(getGetRequestBuilder("/films/popular?count=5"))
                .andExpect(status().isOk())
                .andReturn();
        List<Film> films = fromResult(result, new TypeReference<List<Film>>() {
        });
        assertEquals(5, films.size());
        assertEquals(20, films.get(0).getId());
    }

    @Test
    public void getTopFilmsWithoutZeroCountLimit_ResponseBadRequest() throws Exception {
        createFilms(1);
        MvcResult result = mockMvc.perform(getGetRequestBuilder("/films/popular?count=0"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    private void createFilms(int repeat) throws Exception {
        while (repeat-- > 0) {
            mockMvc.perform(getPostRequestBuilder("/films", film))
                    .andExpect(status().isOk())
                    .andReturn();
        }
    }

    private void createUsers(int repeat) throws Exception {
        while (repeat-- > 0) {
            mockMvc.perform(getPostRequestBuilder("/users", user))
                    .andExpect(status().isOk())
                    .andReturn();
        }
    }
}