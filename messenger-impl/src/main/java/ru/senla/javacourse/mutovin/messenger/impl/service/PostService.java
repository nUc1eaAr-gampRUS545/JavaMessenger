package ru.senla.javacourse.mutovin.messenger.impl.service;

import ru.senla.javacourse.mutovin.messenger.api.dto.PostDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostUpdateRequest;
import ru.senla.javacourse.mutovin.messenger.db.entity.Post;

import java.util.List;

public interface PostService {
    List<PostDto> findPostByCreatorId(Long creatorId);
    PostDto updatePost(PostUpdateRequest request,Long currentUserId);
    PostDto createPost(PostCreateRequest request,Long creatorId);
    PostDto findPostById(Long postId,Long currentUserId);
    List<PostDto> findAllPosts();
    void deletePostById(Long postId, Long currentUserId);
    boolean existsById(Long postId);
}
