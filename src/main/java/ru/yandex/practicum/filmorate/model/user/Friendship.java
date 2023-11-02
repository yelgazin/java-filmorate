package ru.yandex.practicum.filmorate.model.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Friendship {
    boolean confirmed;
    @EqualsAndHashCode.Include()
    long friendId;
}
