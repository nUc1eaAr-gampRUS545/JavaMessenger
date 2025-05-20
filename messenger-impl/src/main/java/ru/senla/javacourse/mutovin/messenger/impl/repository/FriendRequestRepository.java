package ru.senla.javacourse.mutovin.messenger.impl.repository;

import ru.senla.javacourse.mutovin.messenger.db.entity.FriendRequest;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends GenericRepository<FriendRequest, Long> {
    Optional<FriendRequest> findBySenderAndReceiver(String sender,String receiver);
    Optional<List<FriendRequest>> findBySender(Long userId);
    Optional<List<FriendRequest>> findByReceiver(Long userId);
    Optional<FriendRequest> update(FriendRequest friendRequest);
}
