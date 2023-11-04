package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Optional;

public interface UserStorage extends Storage<User, Long> {
    Optional<User> findByEmail(String email);
}
