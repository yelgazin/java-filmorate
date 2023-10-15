package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.BaseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseInMemoryStorage<E extends BaseEntity<K>, K> implements Storage<E, K> {

    private final Map<K, E> entities = new HashMap<>();

    public void add(E entity) {
        validate(entity);
        if (entities.containsKey(entity.getId())) {
            throw new IllegalArgumentException(String.format("Объект c id = %s уже содержится в хранилище.",
                    entity.getId()));
        }
        entities.put(entity.getId(), entity);
    }

    public void update(E entity) {
        validate(entity);
        entities.put(entity.getId(), entity);
    }

    public void remove(E entity) {
        validate(entity);
        entities.remove(entity.getId());
    }

    @Override
    public void removeById(K id) {
        entities.remove(id);
    }

    public boolean contains(E entity) {
        validate(entity);
        return entities.containsKey(entity.getId());
    }

    public E findById(K id) {
        return entities.get(id);
    }

    public List<E> findAll() {
        return new ArrayList<>(entities.values());
    }

    private void validate(E entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Id сущности не может быть null");
        }
    }
}
