package ru.senla.javacourse.mutovin.messenger.impl.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.javacourse.mutovin.messenger.api.dto.PostDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostUpdateRequest;
import ru.senla.javacourse.mutovin.messenger.db.entity.MessageStatus;
import ru.senla.javacourse.mutovin.messenger.db.entity.Post;
import ru.senla.javacourse.mutovin.messenger.db.entity.PostStatus;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;
import ru.senla.javacourse.mutovin.messenger.impl.exception.PostException;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.PostMapper;
import ru.senla.javacourse.mutovin.messenger.impl.repository.PostRepository;
import ru.senla.javacourse.mutovin.messenger.impl.service.PostService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserServiceImpl userService;
    private final PostMapper postMapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "PostService::findPostByCreatorId",key = "#creatorId")
    public List<PostDto> findPostByCreatorId(Long creatorId) {

        List<Post> posts = postRepository.findByCreatorId(creatorId)
                .orElseThrow(() -> new PostException.PostNotFoundException(creatorId));

        return posts.stream().map(postMapper::map).toList();

    }

    @Override
    @Transactional
    public PostDto updatePost(PostUpdateRequest request,Long currentUserId) {

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new PostException.PostNotFoundException(request.getPostId()));

        validatePostAccess(post,currentUserId);

        if (post.getStatus() == PostStatus.ARCHIVED || post.getStatus() == PostStatus.DELETED_BY_ADMIN) {
            throw new PostException.PostNotEditableException(request.getPostId());
        }

        if (request.getContent()==null || request.getContent().isBlank()) {
            throw new PostException.EmptyPostContentException();
        }

        post.setContent(request.getContent().trim());
        post.setStatus(PostStatus.EDITED);
        post.setUpdatedAt(LocalDateTime.now());

        Post result = postRepository.update(post)
                .orElseThrow(() -> new PostException("Не удалось обновить пост"));

        return postMapper.map(result);
    }

    @Override
    @Transactional
    public PostDto createPost(PostCreateRequest request,Long creatorId) {

        if (request==null || request.getContent()==null || request.getTitle()==null) {
            throw new PostException.EmptyPostContentException();
        }
        User creator = userService.findById(creatorId);
        Post post = postMapper.toEntity(request);
        post.setCreator(creator);

        Post result = postRepository.save(post)
                .orElseThrow(() -> new PostException("Не удалось создать пост"));

        return postMapper.map(result);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto findPostById(Long postId,Long currentUserId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException.PostNotFoundException(postId));

        if (post.getStatus()==PostStatus.ARCHIVED || post.getStatus() == PostStatus.DELETED_BY_ADMIN)
            throw new PostException.PostNotDeletableException(postId);

        validatePostAccess(post,currentUserId);
        return postMapper.map(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> findAllPosts() {
        List<Post> posts = postRepository.findAll()
                .orElseThrow(() -> new PostException("Не удалось получить список постов"));
        return posts.stream().map(postMapper::map).toList();
    }

    @Override
    @Transactional
    public void deletePostById(Long postId,Long currentUserId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException.PostNotFoundException(postId));

        validatePostAccess(post,currentUserId);

        if (post.getStatus()==PostStatus.ARCHIVED || post.getStatus() == PostStatus.DELETED_BY_ADMIN)
            throw new PostException.PostNotDeletableException(postId);


        post.setStatus(PostStatus.ARCHIVED);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.update(post);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long postId) {
        return postRepository.existsById(postId);
    }

    private void validatePostAccess(Post post,Long currentUserId) {
        if (!post.getCreator().getId().equals(currentUserId))
            throw new PostException.PostAccessDeniedException(post.getId(),currentUserId);

    }

}
