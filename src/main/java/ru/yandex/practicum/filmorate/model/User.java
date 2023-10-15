package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity<Long> {
    @NotBlank(message = "Электронная почта не может быть пустой и должна содержать символ @")
    @Email(message = "Электронная почта не может быть пустой и должна содержать символ @")
    private String email;
    @NotEmpty(message = "Логин не может быть пустым и содержать пробелы")
    @Pattern(regexp = "\\S+",  message = "Логин не может быть пустым и содержать пробелы")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
    @JsonIgnore
    private Set<Long> friends = new HashSet<>();
}
