package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.exception.RestException;
import ru.yandex.practicum.filmorate.model.user.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest extends AbstractControllerTest {

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setup() {
        user1 = new User();
        user1.setEmail("user1@mail.ru");
        user1.setLogin("loginName1");
        user1.setName("Some name1");
        user1.setBirthday(LocalDate.now().minusYears(40));

        user2 = new User();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("loginName2");
        user2.setName("Some name2");
        user2.setBirthday(LocalDate.now().minusYears(40));

        user3 = new User();
        user3.setEmail("user3@mail.ru");
        user3.setLogin("loginName3");
        user3.setName("Some name3");
        user3.setBirthday(LocalDate.now().minusYears(40));
    }

    @Test
    public void createUserWithNullEmail_ResponseBadRequest() throws Exception {
        user1.setEmail(null);
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void createUserWithEmptyEmail_ResponseBadRequest() throws Exception {
        user1.setEmail("");
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void createUserWithEmailWithoutAtSymbol_ResponseBadRequest() throws Exception {
        user1.setEmail("somemail.com");
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void createUserWithInvalidAtPositionInTheEmail_ResponseBadRequest() throws Exception {
        user1.setEmail("@someemail.com");
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void createUserWithNullLogin_ResponseBadRequest() throws Exception {
        user1.setLogin(null);
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Логин не может быть пустым и содержать пробелы", ex.getMessage());
    }

    @Test
    public void createUserWithEmptyLogin_ResponseBadRequest() throws Exception {
        user1.setLogin("");
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Логин не может быть пустым и содержать пробелы", ex.getMessage());
    }

    @Test
    public void createUserWithSpacesInLogin_ResponseBadRequest() throws Exception {
        user1.setLogin("super login");
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Логин не может быть пустым и содержать пробелы", ex.getMessage());
    }

    @Test
    public void createUserWithBirthdayInTheFuture_ResponseBadRequest() throws Exception {
        user1.setBirthday(LocalDate.now().plusDays(1));
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isBadRequest())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals("Дата рождения не может быть в будущем", ex.getMessage());
    }

    @Test
    public void createUser_CreatedUser() throws Exception {
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isOk())
                .andReturn();

        User createdUser = fromResult(result, User.class);
        user1.setId(1L);
        assertEquals(user1, createdUser);
    }

    @Test
    public void createUserWithEmptyName_CreatedUserWithNameAsLogin() throws Exception {
        user1.setName("");
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isOk())
                .andReturn();

        User createdUser = fromResult(result, User.class);
        assertEquals(user1.getLogin(), createdUser.getName());
    }

    @Test
    public void updateUserWithEmptyName_UpdatedUserWithNameAsLogin() throws Exception {
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user1))
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
        user1.setId(1000L);
        MvcResult result = mockMvc.perform(getPutRequestBuilder("/users", user1))
                .andExpect(status().isNotFound())
                .andReturn();
        RestException ex = fromResult(result, RestException.class);
        assertEquals(String.format("Пользователь с id = %d не найден", user1.getId()), ex.getMessage());
    }

    @Test
    public void updateUser_UpdatedUser() throws Exception {
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user1))
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
        MvcResult result = mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isOk())
                .andReturn();

        user1.setName("Another Name");
        user1.setEmail("another@mail.com");
        result = mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isOk())
                .andReturn();

        result = mockMvc.perform(getGetRequestBuilder("/users"))
                .andExpect(status().isOk())
                .andReturn();

        List<User> users = fromResult(result, new TypeReference<List<User>>() {
        });
        assertEquals(2, users.size());
    }

    @Test
    public void getUserById_ResponseOk() throws Exception {
        mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getPostRequestBuilder("/users", user2))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(getGetRequestBuilder("/users/2"))
                .andExpect(status().isOk())
                .andReturn();
        User requestedUser = fromResult(result, User.class);
        assertEquals(2, requestedUser.getId());
    }

    @Test
    public void getUserByInvalidId_ResponseNotFound() throws Exception {
        mockMvc.perform(getGetRequestBuilder("/users/999"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @Disabled
    public void addFriend_ResponseOkAndAddedForBothUsers() throws Exception {
        mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getPostRequestBuilder("/users", user2))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getPutRequestBuilder("/users/1/friends/2", ""))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result1 = mockMvc.perform(getGetRequestBuilder("/users/1/friends"))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result2 = mockMvc.perform(getGetRequestBuilder("/users/2/friends"))
                .andExpect(status().isOk())
                .andReturn();

        List<User> friendList1 = fromResult(result1, new TypeReference<List<User>>() {
        });
        assertEquals(2, friendList1.get(0).getId());
        List<User> friendList2 = fromResult(result2, new TypeReference<List<User>>() {
        });
        assertEquals(1, friendList2.get(0).getId());
    }

    @Test
    public void addFriendWithInvalidFriendId_ResponseNotFound() throws Exception {
        mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getPutRequestBuilder("/users/1/friends/2", ""))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void addFriendWithInvalidUserId_ResponseNotFound() throws Exception {
        mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getPutRequestBuilder("/users/999/friends/1", ""))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void addFriendTwice_ResponseOk() throws Exception {
        mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getPostRequestBuilder("/users", user2))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getPutRequestBuilder("/users/1/friends/2", ""))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getPutRequestBuilder("/users/2/friends/1", ""))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void removeFriend_ResponseOkRemovedFromBothUsers() throws Exception {
        mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getPostRequestBuilder("/users", user2))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getPutRequestBuilder("/users/1/friends/2", ""))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getDeleteRequestBuilder("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result1 = mockMvc.perform(getGetRequestBuilder("/users/1/friends"))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result2 = mockMvc.perform(getGetRequestBuilder("/users/2/friends"))
                .andExpect(status().isOk())
                .andReturn();

        List<User> friendList1 = fromResult(result1, new TypeReference<List<User>>() {
        });
        assertTrue(friendList1.isEmpty());
        List<User> friendList2 = fromResult(result2, new TypeReference<List<User>>() {
        });
        assertTrue(friendList2.isEmpty());
    }

    @Test
    public void removeFriendWithInvalidUserId_ResponseNotFound() throws Exception {
        mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getPostRequestBuilder("/users", user2))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getPutRequestBuilder("/users/1/friends/2", ""))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getDeleteRequestBuilder("/users/999/friends/2"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void removeFriendWithInvalidFriendId_ResponseNotFound() throws Exception {
        mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getPostRequestBuilder("/users", user2))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getPutRequestBuilder("/users/1/friends/2", ""))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getDeleteRequestBuilder("/users/1/friends/999"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void getFriends_ResponseStatusOkAndReceivedTwoFriends() throws Exception {
        mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(getPostRequestBuilder("/users", user2))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(getPostRequestBuilder("/users", user3))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getPutRequestBuilder("/users/1/friends/2", ""))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(getPutRequestBuilder("/users/1/friends/3", ""))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(getGetRequestBuilder("/users/1/friends"))
                .andExpect(status().isOk())
                .andReturn();

        List<User> friendList = fromResult(result, new TypeReference<List<User>>() {
        });
        assertEquals(2, friendList.size());
    }

    @Test
    public void getFriendsWithInvalidUserId_ResponseStatusNotFound() throws Exception {
        mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(getPostRequestBuilder("/users", user2))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getPutRequestBuilder("/users/1/friends/2", ""))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getGetRequestBuilder("/users/999/friends"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void getCommonFriends_ResponseStatusOkAndReceivedCommonFriends() throws Exception {
        mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(getPostRequestBuilder("/users", user2))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(getPostRequestBuilder("/users", user3))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(getPutRequestBuilder("/users/1/friends/2", ""))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(getPutRequestBuilder("/users/3/friends/2", ""))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(getGetRequestBuilder("/users/1/friends/common/3"))
                .andExpect(status().isOk())
                .andReturn();

        List<User> friendList = fromResult(result, new TypeReference<List<User>>() {
        });
        assertEquals(2, friendList.get(0).getId());
    }

    @Test
    public void getCommonFriendsWithInvalidFirstUserId_ResponseStatusNotFound() throws Exception {
        mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(getGetRequestBuilder("/users/999/friends/common/1"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void getCommonFriendsWithInvalidSecondUserId_ResponseStatusNotFound() throws Exception {
        mockMvc.perform(getPostRequestBuilder("/users", user1))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(getGetRequestBuilder("/users/1/friends/common/999"))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}