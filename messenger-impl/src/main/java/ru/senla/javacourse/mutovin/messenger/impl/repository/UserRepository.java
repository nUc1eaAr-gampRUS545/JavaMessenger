package ru.senla.javacourse.mutovin.messenger.impl.repository;

import ru.senla.javacourse.mutovin.messenger.db.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends GenericRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void deleteById(Long id);
    Optional<Set<User>> findAllByUserIds(Set<Long> userIds);
    Optional<List<User>> findAll();
    Optional<User> update(User user);
    Optional<List<User>> filterUsers(String firstName, String lastName, Integer age, String gender);
}
