package ru.senla.javacourse.mutovin.messenger.impl.repository;

import ru.senla.javacourse.mutovin.messenger.db.entity.Friendship;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends GenericRepository<Friendship, Long> {
    List<Friendship> findByUserId(Long userId);
    Optional<Friendship> findByUserIdAndFriendId(Long userId, Long friendId);
    void deleteByUserIdAndFriendId(Long userId, Long friendId);
} 