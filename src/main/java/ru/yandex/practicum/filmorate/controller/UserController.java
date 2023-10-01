package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> userStorage = new HashMap<>();
    private int numerator;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        int id = ++numerator;
        user.setId(id);

        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }

        userStorage.put(id, user);
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        int id = user.getId();
        if (!userStorage.containsKey(id)) {
            String message = String.format("Пользователь с id = %d не найден", id);
            throw new UserNotFoundException(message);
        }

        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
        userStorage.put(id, user);
        log.info("Обновлен пользователь {}", user);
        return user;
    }

    @GetMapping
    public Collection<User> getAll() {
        return userStorage.values();
    }
}
