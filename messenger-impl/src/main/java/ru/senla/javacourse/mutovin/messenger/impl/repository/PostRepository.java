package ru.senla.javacourse.mutovin.messenger.impl.repository;

import ru.senla.javacourse.mutovin.messenger.db.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends GenericRepository<Post, Long> {

    Optional<List<Post>> findByCreatorId(Long senderId);
    Optional<Post> update(Post post);


}
