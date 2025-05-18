package ru.senla.javacourse.mutovin.messenger.impl.repository;

import ru.senla.javacourse.mutovin.messenger.db.entity.Community;
import ru.senla.javacourse.mutovin.messenger.db.entity.Post;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;

import java.util.List;
import java.util.Optional;

public interface CommunityRepository extends GenericRepository<Community, Long> {
    Optional<List<Community>> getCommunitiesByUserId(Long userId);
    Optional<List<User>> getMembersByCommunityId(Long communityId);
    Optional<Community> update(Community community);
    Optional<List<Post>> getPostsInCommunity(Long communityId);

    boolean isMember(Long userId,Long communityId);

}
