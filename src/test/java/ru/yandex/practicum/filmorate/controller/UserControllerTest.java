package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.exception.RestException;
import ru.yandex.practicum.filmorate.exception.RestExceptionHandler;
import ru.yandex.practicum.filmorate.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractControllerTest {

    private User user;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserController())
                .setControllerAdvice(new RestExceptionHandler())
                .build();

        user = new User();
        user.setEmail("valid@mail.ru");
        user.setLogin("loginName");
        user.setName("Some name");
        user.setBirthday(LocalDate.now().minusYears(40));

    }

    @Test
    public void createUserWithNullEmail_ResponseBadRequest() throws Exception {
        user.setEmail(null);
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void createUserWithEmptyEmail_ResponseBadRequest() throws Exception {
        user.setEmail("");
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void createUserWithEmailWithoutAtSymbol_ResponseBadRequest() throws Exception {
        user.setEmail("somemail.com");
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void createUserWithInvalidAtPositionInTheEmail_ResponseBadRequest() throws Exception {
        user.setEmail("@someemail.com");
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void createUserWithNullLogin_ResponseBadRequest() throws Exception {
        user.setLogin(null);
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Логин не может быть пустым и содержать пробелы", ex.getMessage());
    }

    @Test
    public void createUserWithEmptyLogin_ResponseBadRequest() throws Exception {
        user.setLogin("");
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Логин не может быть пустым и содержать пробелы", ex.getMessage());
    }

    @Test
    public void createUserWithSpacesInLogin_ResponseBadRequest() throws Exception {
        user.setLogin("super login");
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Логин не может быть пустым и содержать пробелы", ex.getMessage());
    }

    @Test
    public void createUserWithBirthdayInTheFuture_ResponseBadRequest() throws Exception {
        user.setBirthday(LocalDate.now().plusDays(1));
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Дата рождения не может быть в будущем", ex.getMessage());
    }

    @Test
    public void createUser_CreatedUser() throws Exception {
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user))
                .andExpect(status().isOk())
                .andReturn();

        User createdUser = fromResult(result, User.class);
        user.setId(1);
        assertEquals(user, createdUser);
    }

    @Test
    public void createUserWithEmptyName_CreatedUserWithNameAsLogin() throws Exception {
        user.setName("");
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user))
                .andExpect(status().isOk())
                .andReturn();

        User createdUser = fromResult(result, User.class);
        assertEquals(user.getLogin(), createdUser.getName());
    }

    @Test
    public void updateUserWithEmptyName_UpdatedUserWithNameAsLogin() throws Exception {
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user))
                .andExpect(status().isOk())
                .andReturn();

        User createdUser = fromResult(result, User.class);
        createdUser.setName("");

        result = mockMvc.perform(getPutRequestBuilder("/users", createdUser))
                .andExpect(status().isOk())
                .andReturn();

        User updatedUser = fromResult(result, User.class);
        assertEquals(createdUser.getLogin(), updatedUser.getName());
    }

    @Test
    public void updateUserWithInvalidId_ResponseNotFound() throws Exception {
        user.setId(1000);
        MvcResult result = mockMvc.perform(getPutRequestBuilder("/users", user))
                .andExpect(status().isNotFound())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals(String.format("Пользователь с id = %d не найден", user.getId()), ex.getMessage());
    }

    @Test
    public void updateUser_UpdatedUser() throws Exception {
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user))
                .andExpect(status().isOk())
                .andReturn();

        User createdUser = fromResult(result, User.class);
        createdUser.setEmail("newmail@mail.ru");
        createdUser.setBirthday(LocalDate.now().minusYears(50));
        createdUser.setLogin("newLogin");
        createdUser.setName("New name");

        result = mockMvc.perform(getPutRequestBuilder("/users", createdUser))
                .andExpect(status().isOk())
                .andReturn();

        User updatedUser = fromResult(result, User.class);
        assertEquals(createdUser, updatedUser);
    }

    @Test
    public void getAllUsers_ReturnerList() throws Exception {
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user))
                .andExpect(status().isOk())
                .andReturn();

        user.setName("Another Name");
        result = mockMvc.perform(getPostRequestBuilder("/users", user))
                .andExpect(status().isOk())
                .andReturn();

        result = mockMvc.perform(getGetRequestBuilder("/users"))
                .andExpect(status().isOk())
                .andReturn();

        List<User> users = fromResult(result,  new TypeReference<List<User>>() {});
        assertEquals(2, users.size());
    }
}