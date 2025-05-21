package ru.senla.javacourse.mutovin.messenger.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.senla.javacourse.mutovin.messenger.api.dto.PostDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostUpdateRequest;
import ru.senla.javacourse.mutovin.messenger.db.entity.Post;
import ru.senla.javacourse.mutovin.messenger.db.entity.PostStatus;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;
import ru.senla.javacourse.mutovin.messenger.impl.exception.PostException;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.PostMapper;
import ru.senla.javacourse.mutovin.messenger.impl.repository.PostRepository;
import ru.senla.javacourse.mutovin.messenger.impl.service.impl.PostServiceImpl;
import ru.senla.javacourse.mutovin.messenger.impl.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceImplTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private PostMapper postMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPost_success() {
        PostCreateRequest request = new PostCreateRequest("Title", "Content");
        Long creatorId = 1L;
        User user = new User();
        user.setId(creatorId);
        Post post = postMapper.toEntity(request);
        Post savedPost = new Post();
        PostDto expectedDto = PostDto.builder().build();

        when(userService.findById(creatorId)).thenReturn(user);
        when(postRepository.save(any())).thenReturn(Optional.of(savedPost));
        when(postMapper.map(savedPost)).thenReturn(expectedDto);

        PostDto result = postService.createPost(request, creatorId);

        assertEquals(expectedDto, result);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void createPost_throwWhenContentIsEmpty() {
        PostCreateRequest request = new PostCreateRequest(null, null);
        assertThrows(PostException.EmptyPostContentException.class,
                () -> postService.createPost(request, 1L));
    }

    @Test
    void updatePost_success() {
        Long userId = 1L;
        Long postId = 2L;
        PostUpdateRequest request = new PostUpdateRequest(postId, "Updated content");
        Post post = new Post();
        post.setId(postId);
        post.setCreator(new User());
        post.getCreator().setId(userId);
        post.setStatus(PostStatus.PUBLISHED);
        Post updatedPost = new Post();
        PostDto dto = PostDto.builder().build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.update(post)).thenReturn(Optional.of(updatedPost));
        when(postMapper.map(updatedPost)).thenReturn(dto);

        PostDto result = postService.updatePost(request, userId);

        assertEquals(dto, result);
    }

    @Test
    void updatePost_throwIfUserNotCreator() {
        Long userId = 1L;
        Long postId = 2L;
        Post post = new Post();
        post.setId(postId);
        post.setCreator(new User());
        post.getCreator().setId(99L);
        post.setStatus(PostStatus.PUBLISHED);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        PostUpdateRequest request = new PostUpdateRequest(postId, "test");

        assertThrows(PostException.PostAccessDeniedException.class,
                () -> postService.updatePost(request, userId));
    }

    @Test
    void deletePost_success() {
        Long postId = 1L;
        Long userId = 2L;
        Post post = new Post();
        post.setId(postId);
        post.setStatus(PostStatus.PUBLISHED);
        User creator = new User();
        creator.setId(userId);
        post.setCreator(creator);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.update(any())).thenReturn(Optional.of(post));

        assertDoesNotThrow(() -> postService.deletePostById(postId, userId));
        assertEquals(PostStatus.ARCHIVED, post.getStatus());
    }

    @Test
    void findPostById_success() {
        Long postId = 1L;
        Long userId = 2L;
        Post post = new Post();
        post.setId(postId);
        User creator = new User();
        creator.setId(userId);
        post.setCreator(creator);
        post.setStatus(PostStatus.PUBLISHED);
        PostDto dto = PostDto.builder().build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.map(post)).thenReturn(dto);

        PostDto result = postService.findPostById(postId, userId);

        assertEquals(dto, result);
    }

    @Test
    void findAllPosts_success() {
        List<Post> posts = List.of(new Post(), new Post());
        PostDto dto = PostDto.builder().build();
        List<PostDto> dtos = List.of(dto, dto);

        when(postRepository.findAll()).thenReturn(Optional.of(posts));
        when(postMapper.map(posts.get(0))).thenReturn(dtos.get(0));
        when(postMapper.map(posts.get(1))).thenReturn(dtos.get(1));

        List<PostDto> result = postService.findAllPosts();

        assertEquals(2, result.size());
    }

    @Test
    void existsById_returnsTrue() {
        when(postRepository.existsById(1L)).thenReturn(true);
        assertTrue(postService.existsById(1L));
    }

    @Test
    void findPostByCreatorId_success() {
        Long creatorId = 1L;
        List<Post> posts = List.of(new Post(), new Post());
        PostDto dto = PostDto.builder().build();
        List<PostDto> dtos = List.of(dto, dto);

        when(postRepository.findByCreatorId(creatorId)).thenReturn(Optional.of(posts));
        when(postMapper.map(posts.get(0))).thenReturn(dtos.get(0));
        when(postMapper.map(posts.get(1))).thenReturn(dtos.get(1));

        List<PostDto> result = postService.findPostByCreatorId(creatorId);

        assertEquals(2, result.size());
    }
}
