package ru.senla.javacourse.mutovin.messenger.impl.repository;

import ru.senla.javacourse.mutovin.messenger.db.entity.Subscribe;

import java.util.List;

public interface SubscribeRepository extends GenericRepository<Subscribe, Long> {
    List<Subscribe> findByUserId(Long userId);
    void deleteByUserIdAndCommunityId(Long userId,Long communityId);
}
