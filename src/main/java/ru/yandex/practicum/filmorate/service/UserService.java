package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private Long numerator = 0L;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        Long id = ++numerator;
        user.setId(id);
        ensureName(user);
        userStorage.add(user);
        log.info("Добавлен пользователь {}", user);
        return user;
    }
    public User update(User user) {
        Long id = user.getId();
        User savedUser = userStorage.findById(id);

        if (savedUser == null) {
            String message = String.format("Пользователь с id = %d не найден", id);
            throw new UserNotFoundException(message);
        }
        ensureName(user);
        savedUser.setName(user.getName());
        savedUser.setLogin(user.getLogin());
        savedUser.setEmail(user.getEmail());
        savedUser.setBirthday(user.getBirthday());
        userStorage.update(savedUser);
        log.info("Обновлен пользователь {}", user);
        return user;
    }
    public Collection<User> getAll() {
        return userStorage.findAll();
    }

    public User getById(Long userId) {
        User user = userStorage.findById(userId);
        if (user == null) {
            String message = String.format("Пользователь с id = %d не найден", userId);
            throw new UserNotFoundException(message);
        }

        return user;
    }

    public Collection<User> getFriendsByUserId(Long id) {
        return  userStorage.findById(id).getFriends()
                .stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        if (user == null) {
            String message = String.format("Пользователь с id = %d не найден", userId);
            throw new UserNotFoundException(message);
        }
        if (friend == null) {
            String message = String.format("Пользователь с id = %d не найден", friendId);
            throw new UserNotFoundException(message);
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        if (user == null) {
            String message = String.format("Пользователь с id = %d не найден", userId);
            throw new UserNotFoundException(message);
        }
        if (friend == null) {
            String message = String.format("Пользователь с id = %d не найден", friendId);
            throw new UserNotFoundException(message);
        }

        user.getFriends().remove(userId);
        friend.getFriends().remove(userId);
    }

    public Collection<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        User firstUser = userStorage.findById(firstUserId);
        User secondUser = userStorage.findById(secondUserId);

        if (firstUser == null) {
            String message = String.format("Пользователь с id = %d не найден", firstUserId);
            throw new UserNotFoundException(message);
        }
        if (secondUser == null) {
            String message = String.format("Пользователь с id = %d не найден", secondUserId);
            throw new UserNotFoundException(message);
        }

        Set<Long> commonIds = new HashSet<>(firstUser.getFriends());
        commonIds.retainAll(secondUser.getFriends());
        return commonIds.stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    private void ensureName(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
