# Backend REST service Filmorate

## Сервис подбора фильмов по оценкам пользователей
Позволяет пользователям ставить лайки фильмам и провдить по ним поиск.
> **_Проект является промежуточным, дальнейшая работа велась в групповом репозитории._**

## Диаграмма отношений объектов (Entity Relationship Diagram - ERD)

![](./doc/ERD.jpg)

### Получение пользователя по id
``` roomsql
SELECT user_id,
       email,
       login,
       name,
       birthday
FROM users
WHERE user_id = ?;
```

### Получение всех пользователей
``` roomsql
SELECT user_id,
       email,
       login,
       name,
       birthday
FROM users;
```

### Получение друзей пользователя
В таблице user_friend хранится связь пользователь -> друг. Колонка confirmed хранит булевое значение:
1 - связь подтверждена,
0 - связь не подтверждена.
Считаем, что подтвержденная связь является однонаправленной.
Для получения списка всех друзей пользователя выполняем запрос:
``` roomsql
SELECT friend_id AS user_id
FROM user_friend
WHERE confirmed = TRUE
AND user_id = ?
```

### Получение идентификаторов популярных фильмов
``` roomsql
SELECT f.film_id
FROM film f
LEFT JOIN film_like l
ON f.film_id = l.film_id;
GROUP BY f.film_id
ORDER BY COUNT(l.*);
```

## Технологический стек
![java](https://img.shields.io/badge/java-%23ed8b00.svg?logo=openjdk&logoColor=white&style=flat)
![spring](https://img.shields.io/badge/spring-%236db33f.svg?logo=spring&logoColor=white&style=flat)
![postgres](https://img.shields.io/badge/postgres-%23336791.svg?logo=postgresql&logoColor=white&style=flat)
![postman](https://img.shields.io/badge/Postman-FF6C37?style=flat&logo=postman&logoColor=white)
![maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=flat&logo=Apache%20Maven&logoColor=white)
