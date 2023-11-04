package ru.yandex.practicum.filmorate.storage.user.mem;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.BaseInMemoryStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Optional;

@Component
public class InMemoryUserStorage extends BaseInMemoryStorage<User, Long> implements UserStorage {
    public Optional<User> findByEmail(String email) {
        return findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny();
    }
}
