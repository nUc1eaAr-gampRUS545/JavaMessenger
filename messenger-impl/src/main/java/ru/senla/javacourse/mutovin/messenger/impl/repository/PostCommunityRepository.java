package ru.senla.javacourse.mutovin.messenger.impl.repository;

import ru.senla.javacourse.mutovin.messenger.db.entity.PostCommunity;

import java.util.List;

public interface PostCommunityRepository extends GenericRepository<PostCommunity, Long> {

    List<PostCommunity> findByUserId(Long communityId);

    void deleteByPostIdAndCommunityId(Long postId,Long communityId);
}
