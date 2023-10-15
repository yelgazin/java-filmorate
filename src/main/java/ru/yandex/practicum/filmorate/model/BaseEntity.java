package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public abstract class BaseEntity<K> {
    private K id;
}
