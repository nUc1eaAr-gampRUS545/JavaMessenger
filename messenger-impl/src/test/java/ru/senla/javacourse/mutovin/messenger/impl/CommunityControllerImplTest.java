package ru.senla.javacourse.mutovin.messenger.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import ru.senla.javacourse.mutovin.messenger.api.dto.CommunityDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.CommunityCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostCreateRequest;
import ru.senla.javacourse.mutovin.messenger.impl.controller.CommunityControllerImpl;
import ru.senla.javacourse.mutovin.messenger.impl.service.CommunityService;
import ru.senla.javacourse.mutovin.messenger.impl.service.UserService;

import java.nio.file.AccessDeniedException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CommunityControllerImplTest {

    private CommunityService communityService;
    private UserService userService;
    private CommunityControllerImpl controller;

    @BeforeEach
    void setUp() {
        communityService = mock(CommunityService.class);
        userService = mock(UserService.class);
        controller = new CommunityControllerImpl(communityService, userService);
    }

    @Test
    void testCreateCommunity() {
        CommunityCreateRequest request = new CommunityCreateRequest();
        CommunityDto dto = CommunityDto.builder().build();
        when(communityService.createCommunity(request)).thenReturn(dto);

        ResponseEntity<?> response = controller.createCommunity(request);

        assertEquals(200, response.getStatusCodeValue());
        verify(communityService).createCommunity(request);
    }

    @Test
    void testGetCommunity() {
        Long communityId = 1L;
        CommunityDto dto = CommunityDto.builder().build();
        when(communityService.getCommunity(communityId)).thenReturn(dto);

        ResponseEntity<?> response = controller.getCommunity(communityId);

        assertEquals(200, response.getStatusCodeValue());
        verify(communityService).getCommunity(communityId);
    }

    @Test
    void testCreatePostInCommunity() throws AccessDeniedException {
        Long communityId = 1L;
        Long userId = 42L;
        PostCreateRequest request = new PostCreateRequest();
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        CommunityDto dto = CommunityDto.builder().build();
        when(communityService.createPostInCommunity(userId, communityId, request)).thenReturn(dto);

        ResponseEntity<?> response = controller.createPostInCommunity(userDetails, communityId, request);

        assertEquals(200, response.getStatusCodeValue());
        verify(communityService).createPostInCommunity(userId, communityId, request);
    }

    @Test
    void testUpdateCommunity() {
        Long communityId = 1L;
        CommunityCreateRequest request = new CommunityCreateRequest();
        CommunityDto dto = CommunityDto.builder().build();
        when(communityService.updateCommunity(communityId, request)).thenReturn(dto);

        ResponseEntity<?> response = controller.updateCommunity(communityId, request);

        assertEquals(200, response.getStatusCodeValue());
        verify(communityService).updateCommunity(communityId, request);
    }

    @Test
    void testDeleteCommunity() {
        Long communityId = 1L;

        ResponseEntity<?> response = controller.deleteCommunity(communityId);

        assertEquals(200, response.getStatusCodeValue());
        verify(communityService).deleteCommunity(communityId);
    }

    @Test
    void testJoinCommunity() {
        Long communityId = 1L;
        Long userId = 123L;
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user");
        CommunityDto dto = CommunityDto.builder().build();
        when(communityService.joinCommunity(userId, communityId)).thenReturn(dto);

        ResponseEntity<?> response = controller.joinCommunity(userDetails, communityId);

        assertEquals(200, response.getStatusCodeValue());
        verify(communityService).joinCommunity(userId, communityId);
    }

    @Test
    void testLeaveCommunity() {
        Long communityId = 1L;
        Long userId = 123L;
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user");

        ResponseEntity<?> response = controller.leaveCommunity(userDetails, communityId);

        assertEquals(200, response.getStatusCodeValue());
        verify(communityService).leaveCommunity(userId, communityId);
    }

    @Test
    void testGetCommunityMembers() {
        Long communityId = 1L;
        UserDto userDto = UserDto.builder().build();
        List<UserDto> members = List.of(userDto);
        when(communityService.getCommunityMembers(communityId)).thenReturn(members);

        ResponseEntity<?> response = controller.getCommunityMembers(communityId);

        assertEquals(200, response.getStatusCodeValue());
        verify(communityService).getCommunityMembers(communityId);
    }

    @Test
    void testGetCommunitiesByUser() {
        Long userId = 1L;
        CommunityDto dto = CommunityDto.builder().build();
        List<CommunityDto> communities = List.of(dto);
        when(communityService.getUserCommunities(userId)).thenReturn(communities);

        ResponseEntity<?> response = controller.getCommunitiesByUser(userId);

        assertEquals(200, response.getStatusCodeValue());
        verify(communityService).getUserCommunities(userId);
    }
}

