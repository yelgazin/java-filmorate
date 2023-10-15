package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public abstract class BaseEntity<ID> {
    private ID id;
}
