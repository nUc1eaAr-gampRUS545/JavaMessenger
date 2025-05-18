package ru.senla.javacourse.mutovin.messenger.impl.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.javacourse.mutovin.messenger.api.dto.CommunityDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.PostDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.CommunityCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostCreateRequest;
import ru.senla.javacourse.mutovin.messenger.db.entity.*;
import ru.senla.javacourse.mutovin.messenger.impl.exception.ResourceNotFoundException;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.CommunityMapper;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.PostMapper;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.UserMapper;
//import ru.senla.javacourse.mutovin.messenger.impl.repository.CommunityRepository;
import ru.senla.javacourse.mutovin.messenger.impl.repository.*;
import ru.senla.javacourse.mutovin.messenger.impl.service.CommunityService;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final SubscribeRepository subscribeRepository;
    private final PostCommunityRepository postCommunityRepository;
    private final CommunityMapper communityMapper;
    private final UserMapper userMapper;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public CommunityDto createCommunity(CommunityCreateRequest request) {
        Community community = request.toEntity();
        Community result = communityRepository.save(community)
                .orElseThrow(() -> new IllegalArgumentException("Не удалось создать сообщество"));
        return communityMapper.map(result);
    }

    @Override
    public CommunityDto getCommunity(Long id) {
        Community result = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Не удалось найти сообщество"));
        List<User> members = communityRepository.getMembersByCommunityId(id)
                .orElseThrow(() -> new RuntimeException("Не удалось получить подписчиков"));

        List<Post> posts = communityRepository.getPostsInCommunity(id)
                .orElseThrow(() -> new RuntimeException("Не удалось получить подписчиков"));

        List<UserDto> membersDto = members.stream().map(userMapper::map).toList();
        List<PostDto> postsDto = posts.stream().map(postMapper::map).toList();
        CommunityDto dto = communityMapper.map(result);
        dto.setMembers(membersDto);
        dto.setPosts(postsDto);
        return dto;
    }

    @Override
    @Transactional
    public CommunityDto updateCommunity(Long id,CommunityCreateRequest request) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Не удалось создать сообщество"));
        community.setName(request.getName());
        community.setDescription(request.getDescription());
        community.setId(id);
        Community result = communityRepository.update(community)
                .orElseThrow(() -> new IllegalArgumentException("Не удалось обновить сообщество"));
        return communityMapper.map(result);
    }

    @Override
    @Transactional
    public void deleteCommunity(Long id) {
        communityRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CommunityDto joinCommunity(Long userId,Long communityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));

        if (!communityRepository.isMember(userId,communityId)) {
            Subscribe subscribe = new Subscribe();
            subscribe.setCommunity(community);
            subscribe.setMember(user);
            subscribe.setCreatedAt(LocalDateTime.now());
            subscribeRepository.save(subscribe);

            return getCommunity(communityId);
        }
        return communityMapper.map(community);
    }

    @Transactional
    @Override
    public CommunityDto createPostInCommunity(Long userId,Long communityId,PostCreateRequest request) throws AccessDeniedException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));
        Post post = request.toEntity();
        post.setCreator(user);
        if (communityRepository.isMember(userId,communityId)) {
            PostCommunity postCommunity = new PostCommunity();
            postCommunity.setCommunity(community);
            postCommunity.setPost(post);
            postCommunity.setCreatedAt(LocalDateTime.now());
            postCommunityRepository.save(postCommunity);

            return getCommunity(communityId);
        }
        else throw new AccessDeniedException("Вы не состоите в сообществе");

    }

    @Override
    @Transactional
    public void leaveCommunity(Long userId,Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));
        if (communityRepository.isMember(userId,communityId)) {
            subscribeRepository.deleteByUserIdAndCommunityId(userId,communityId);
        }

    }

    @Override
    public List<UserDto> getCommunityMembers(Long communityId) {
        List<User> members = communityRepository.getMembersByCommunityId(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Участники сообщества не найдены")
                );
        return members.stream().map(userMapper::map).toList();
    }

    @Override
    public List<CommunityDto> getUserCommunities(Long userId) {

        List<Community> result = communityRepository.getCommunitiesByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Не удалось получить сообщества"));

        return result.stream().map(i -> communityMapper.map(i)).toList();
    }


}
