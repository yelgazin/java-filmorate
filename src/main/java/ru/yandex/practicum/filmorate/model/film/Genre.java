package ru.yandex.practicum.filmorate.model.film;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.model.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@NoArgsConstructor
public class Genre extends BaseEntity<Integer> implements Comparable<Genre> {
    String name;

    @Override
    public int compareTo(Genre o) {
        return getId().compareTo(o.getId());
    }
}
