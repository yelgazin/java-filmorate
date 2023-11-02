package ru.yandex.practicum.filmorate.storage.user.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.user.Friendship;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("databaseUserStorage")
@RequiredArgsConstructor
public class DatabaseUserStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    @Override
    public void add(User user) {
        String query = "INSERT INTO users (user_id, email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(query,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());

        user.getFriendships().forEach(friendship -> addFriendship(user, friendship));
    }

    @Transactional
    @Override
    public void update(User user) {
        String query = "UPDATE users " +
                "SET email = ?," +
                "    login = ?," +
                "    name = ?," +
                "    birthday = ?" +
                "WHERE user_id = ?";

        jdbcTemplate.update(query,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        // Process friendships
        //
        Set<Friendship> newFriendships = user.getFriendships();
        Set<Friendship> existFriendships = findFriendshipsByUserId(user.getId());

        // Remove friendships
        Set<Friendship> toRemoveFriendships = new HashSet<>(existFriendships);
        toRemoveFriendships.removeAll(newFriendships);
        toRemoveFriendships.forEach(f -> removeFriendship(user, f));

        // Update friendships (only if confirmation status was changed)
        // Be aware of Friendship equals / hashCode, it uses only friendId
        Set<Friendship> toUpdateFriendships = new HashSet<>(existFriendships);
        toUpdateFriendships.retainAll(newFriendships);
        Map<Long, Boolean> newConfirmationsStatuses = newFriendships.stream()
                .collect(Collectors.toMap(Friendship::getFriendId, Friendship::isConfirmed));
        toUpdateFriendships.stream()
                .filter(f -> f.isConfirmed() != newConfirmationsStatuses.get(f.getFriendId()))
                .forEach(f -> updateFriendship(user, f));

        // Add new friendships
        Set<Friendship> toAddFriendships = new HashSet<>(newFriendships);
        toAddFriendships.removeAll(existFriendships);
        toAddFriendships.forEach(f -> addFriendship(user, f));
    }

    @Override
    public boolean contains(User user) {
        return findById(user.getId()).isPresent();
    }

    @Override
    public void remove(User user) {
        removeById(user.getId());
    }

    @Transactional
    @Override
    public void removeById(Long id) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM film WHERE film_id = ?", id);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jdbcTemplate.query(
                "SELECT user_id, email, login, name, birthday FROM users WHERE user_id = ?",
                this::mapUser, id).stream().findAny();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jdbcTemplate.query(
                "SELECT user_id, email, login, name, birthday FROM users WHERE email = ?",
                this::mapUser, email).stream().findAny();
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(
                "SELECT user_id, email, login, name, birthday FROM users",
                this::mapUser);
    }

    private User mapUser(ResultSet rs, int rowNum) throws SQLException {
        Long userId = rs.getLong("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        User user = User.builder()
                .id(userId)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .build();

        user.setFriendships(findFriendshipsByUserId(userId));
        return user;
    }

    private Friendship mapFriendship(ResultSet rs, int rowNum) throws SQLException {
        long friendId = rs.getLong("friend_id");
        boolean confirmed = rs.getBoolean("confirmed");
        return Friendship.builder()
                .friendId(friendId)
                .confirmed(confirmed)
                .build();
    }

    private Set<Friendship> findFriendshipsByUserId(Long userId) {
        return new HashSet<>(jdbcTemplate.query("SELECT friend_id, confirmed FROM user_friend WHERE user_id = ?",
                this::mapFriendship,
                userId));
    }

    private void addFriendship(User user, Friendship friendship) {
        String query = "INSERT INTO user_friend (user_id, friend_id, confirmed) VALUES (?, ?, ?)";
        jdbcTemplate.update(query, user.getId(), friendship.getFriendId(), friendship.isConfirmed());
    }

    private void updateFriendship(User user, Friendship friendship) {
        String query = "UPDATE user_friend SET confirmed = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(query, user.getId(), friendship.getFriendId(), friendship.isConfirmed());
    }

    private void removeFriendship(User user, Friendship friendship) {
        String query = "DELETE FROM user_friend WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(query, user.getId(), friendship.getFriendId());
    }
}


