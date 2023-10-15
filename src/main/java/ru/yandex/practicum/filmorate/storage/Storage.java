package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.BaseEntity;

import java.util.List;

public interface Storage<E extends BaseEntity<ID>, ID> {
    void add (E entity);
    void update(E entity);
    boolean contains(E entity);
    void remove(E entity);
    void removeById(ID id);
    E findById(ID id);
    List<E> findAll();
}
