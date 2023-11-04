package ru.yandex.practicum.filmorate.model.film;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.model.BaseEntity;
import ru.yandex.practicum.filmorate.validation.DateAfterOrEqual;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@NoArgsConstructor
public class Film extends BaseEntity<Long> {
    @NotBlank(message = "Название не может быть пустым")
    String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    String description;
    @DateAfterOrEqual(minDate = "1895-12-28", message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    int duration;
    @Builder.Default
    Set<Genre> genres = new TreeSet<>(); // Тесты спринта требуют сортировку
    @NotNull(message = "Рейтинг MPA не может быть пустым")
    Mpa mpa;
    @Builder.Default
    @JsonIgnore
    Set<Long> likes = new HashSet<>();
}
