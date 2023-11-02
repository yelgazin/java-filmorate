package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.user.Friendship;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("databaseUserStorage")
    private final UserStorage userStorage;
    private Long numerator = 0L;

    public User create(User user) {
        validate(user);
        Long id = ++numerator;
        user.setId(id);
        ensureName(user);
        userStorage.add(user);
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    public User update(User user) {
        Long id = user.getId();
        User savedUser = userStorage.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = %d не найден", id));

        validate(user);
        ensureName(user);
        BeanUtils.copyProperties(user, savedUser, "friendships");
        userStorage.update(savedUser);
        log.info("Обновлен пользователь {}", savedUser);
        return savedUser;
    }

    public Collection<User> getAll() {
        return userStorage.findAll();
    }

    public User getById(Long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = %d не найден", userId));
    }

    public Collection<User> getFriendsByUserId(Long userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = %d не найден", userId));

        return user.getFriendships()
                .stream()
                .map(Friendship::getFriendId)
                .map(userStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = %d не найден", userId));

        userStorage.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = %d не найден", friendId));

        user.getFriendships().add(Friendship.builder().friendId(friendId).build());
        userStorage.update(user);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = %d не найден", userId));

        userStorage.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = %d не найден", friendId));

        user.getFriendships().remove(Friendship.builder().friendId(friendId).build());
        userStorage.update(user);
    }

    public Collection<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        User firstUser = userStorage.findById(firstUserId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = %d не найден", firstUserId));

        User secondUser = userStorage.findById(secondUserId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = %d не найден", secondUserId));

        Set<Long> commonIds = firstUser.getFriendships().stream()
                .map(Friendship::getFriendId).collect(Collectors.toSet());
        commonIds.retainAll(secondUser.getFriendships().stream()
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet()));
        return commonIds.stream()
                .map(userStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private void ensureName(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void validate(User user) {
        // Validate email
        Optional<User> foundUser = userStorage.findByEmail(user.getEmail());

        if (foundUser.isPresent() && !Objects.equals(foundUser.get().getId(), user.getId())) {
            throw new UserAlreadyExistsException("Пользователь с почтой %s уже существует", user.getEmail());
        }
    }
}
