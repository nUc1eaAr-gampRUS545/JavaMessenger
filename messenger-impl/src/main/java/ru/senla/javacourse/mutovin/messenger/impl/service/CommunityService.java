package ru.senla.javacourse.mutovin.messenger.impl.service;

import org.springframework.transaction.annotation.Transactional;
import ru.senla.javacourse.mutovin.messenger.api.dto.CommunityDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.CommunityCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostCreateRequest;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface CommunityService {
    CommunityDto createCommunity(CommunityCreateRequest request);
    CommunityDto  getCommunity(Long id);
    CommunityDto  updateCommunity(Long id, CommunityCreateRequest request);
    void deleteCommunity(Long id);
    CommunityDto  joinCommunity(Long userId, Long communityId);
    List<UserDto> getCommunityMembers(Long communityId);
    List<CommunityDto> getUserCommunities(Long userId);

    @Transactional
    CommunityDto createPostInCommunity(Long userId,Long communityId,PostCreateRequest request) throws AccessDeniedException;

    void leaveCommunity(Long userId,Long communityId);
}
