package ru.senla.javacourse.mutovin.messenger.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.senla.javacourse.mutovin.messenger.api.dto.*;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.CommunityCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostCreateRequest;
import ru.senla.javacourse.mutovin.messenger.db.entity.*;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.CommunityMapper;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.PostMapper;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.UserMapper;
import ru.senla.javacourse.mutovin.messenger.impl.repository.*;
import ru.senla.javacourse.mutovin.messenger.impl.service.impl.CommunityServiceImpl;

import java.nio.file.AccessDeniedException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommunityServiceImplTest {

    @InjectMocks
    private CommunityServiceImpl communityService;

    @Mock
    private CommunityRepository communityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscribeRepository subscribeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostCommunityRepository postCommunityRepository;

    @Mock
    private CommunityMapper communityMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PostMapper postMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCommunity_shouldReturnDto() {
        CommunityCreateRequest request = new CommunityCreateRequest();
        Community community = new Community();
        Community saved = new Community();
        CommunityDto dto = new CommunityDto();

        when(communityRepository.save(any())).thenReturn(Optional.of(saved));
        when(communityMapper.map(saved)).thenReturn(dto);

        CommunityDto result = communityService.createCommunity(request);

        assertEquals(dto, result);
        verify(communityRepository).save(any());
    }

    @Test
    void getCommunity_shouldReturnCommunityDto() {
        Long id = 1L;
        Community community = new Community();
        List<User> members = List.of(new User());
        List<Post> posts = List.of(new Post());
        CommunityDto dto = new CommunityDto();

        when(communityRepository.findById(id)).thenReturn(Optional.of(community));
        when(communityRepository.getMembersByCommunityId(id)).thenReturn(Optional.of(members));
        when(communityRepository.getPostsInCommunity(id)).thenReturn(Optional.of(posts));
        when(communityMapper.map(community)).thenReturn(dto);
        when(userMapper.map(any())).thenReturn(new UserDto());
        when(postMapper.map(any())).thenReturn(new PostDto());

        CommunityDto result = communityService.getCommunity(id);

        assertNotNull(result);
        assertEquals(dto, result);
    }

    @Test
    void updateCommunity_shouldReturnUpdatedDto() {
        Long id = 1L;
        CommunityCreateRequest request = new CommunityCreateRequest();
        Community existing = new Community();
        Community updated = new Community();
        CommunityDto dto = new CommunityDto();

        when(communityRepository.findById(id)).thenReturn(Optional.of(existing));
        when(communityRepository.update(existing)).thenReturn(Optional.of(updated));
        when(communityMapper.map(updated)).thenReturn(dto);

        CommunityDto result = communityService.updateCommunity(id, request);

        assertEquals(dto, result);
    }

    @Test
    void deleteCommunity_shouldCallDelete() {
        Long id = 1L;
        communityService.deleteCommunity(id);
        verify(communityRepository).deleteById(id);
    }

    @Test
    void joinCommunity_shouldReturnCommunityDto() {
        Long userId = 1L;
        Long communityId = 2L;
        User user = new User();
        Community community = new Community();
        CommunityDto dto = new CommunityDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
        when(communityRepository.isMember(userId, communityId)).thenReturn(false);
        when(communityRepository.getMembersByCommunityId(communityId)).thenReturn(Optional.of(List.of()));
        when(communityRepository.getPostsInCommunity(communityId)).thenReturn(Optional.of(List.of()));
        when(communityMapper.map(community)).thenReturn(dto);

        CommunityDto result = communityService.joinCommunity(userId, communityId);

        assertNotNull(result);
        verify(subscribeRepository).save(any(Subscribe.class));
    }

    @Test
    void createPostInCommunity_shouldSucceed() throws AccessDeniedException {
        Long userId = 1L;
        Long communityId = 2L;
        User user = new User();
        Community community = new Community();
        Post post = new Post();
        Post savedPost = new Post();

        PostCreateRequest request = mock(PostCreateRequest.class);
        //when(request.toEntity()).thenReturn(post);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
        when(postRepository.save(post)).thenReturn(Optional.of(savedPost));
        when(communityRepository.isMember(userId, communityId)).thenReturn(true);
        when(communityRepository.getMembersByCommunityId(communityId)).thenReturn(Optional.of(List.of()));
        when(communityRepository.getPostsInCommunity(communityId)).thenReturn(Optional.of(List.of()));
        when(communityMapper.map(any())).thenReturn(new CommunityDto());

        CommunityDto result = communityService.createPostInCommunity(userId, communityId, request);

        assertNotNull(result);
        verify(postCommunityRepository).save(any(PostCommunity.class));
    }

    @Test
    void leaveCommunity_shouldCallDeleteIfMember() {
        Long userId = 1L;
        Long communityId = 2L;
        Community community = new Community();

        when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
        when(communityRepository.isMember(userId, communityId)).thenReturn(true);

        communityService.leaveCommunity(userId, communityId);

        verify(subscribeRepository).deleteByUserIdAndCommunityId(userId, communityId);
    }

    @Test
    void getCommunityMembers_shouldReturnDtos() {
        Long id = 1L;
        List<User> users = List.of(new User());
        UserDto userDto = new UserDto();

        when(communityRepository.getMembersByCommunityId(id)).thenReturn(Optional.of(users));
        when(userMapper.map(any())).thenReturn(userDto);

        List<UserDto> result = communityService.getCommunityMembers(id);

        assertEquals(1, result.size());
    }

    @Test
    void getUserCommunities_shouldReturnDtos() {
        Long userId = 1L;
        Community community = new Community();
        CommunityDto dto = new CommunityDto();

        when(communityRepository.getCommunitiesByUserId(userId)).thenReturn(Optional.of(List.of(community)));
        when(communityMapper.map(community)).thenReturn(dto);

        List<CommunityDto> result = communityService.getUserCommunities(userId);

        assertEquals(1, result.size());
    }
}
