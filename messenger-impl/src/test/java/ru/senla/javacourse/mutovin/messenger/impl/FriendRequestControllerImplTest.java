package ru.senla.javacourse.mutovin.messenger.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import ru.senla.javacourse.mutovin.messenger.api.dto.FriendRequestDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.SuccessResponse;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;
import ru.senla.javacourse.mutovin.messenger.impl.controller.FriendRequestControllerImpl;
import ru.senla.javacourse.mutovin.messenger.impl.service.FriendRequestService;
import ru.senla.javacourse.mutovin.messenger.impl.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FriendRequestControllerImplTest {

    @InjectMocks
    private FriendRequestControllerImpl controller;

    @Mock
    private FriendRequestService friendRequestService;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendRequest_ReturnsSuccessResponse() {
        String username = "user1";
        Long senderId = 1L;
        Long receiverId = 2L;

        when(userDetails.getUsername()).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(User.builder().id(senderId).username(username).build());
        FriendRequestDto dto = FriendRequestDto.builder().build();
        when(friendRequestService.sendRequest(senderId, receiverId)).thenReturn(dto);

        ResponseEntity<?> response = controller.sendRequest(userDetails, receiverId);

        assertThat(response.getBody()).isInstanceOf(SuccessResponse.class);
        verify(friendRequestService).sendRequest(senderId, receiverId);
    }

    @Test
    void acceptRequest_ReturnsSuccessResponse() {
        Long requestId = 10L;
        FriendRequestDto dto = FriendRequestDto.builder().build();
        when(friendRequestService.acceptRequest(requestId)).thenReturn(dto);

        ResponseEntity<?> response = controller.acceptRequest(requestId);

        assertThat(response.getBody()).isInstanceOf(SuccessResponse.class);
        verify(friendRequestService).acceptRequest(requestId);
    }

    @Test
    void rejectRequest_ReturnsSuccessResponse() {
        Long requestId = 20L;
        FriendRequestDto dto = FriendRequestDto.builder().build();
        when(friendRequestService.rejectRequest(requestId)).thenReturn(dto);

        ResponseEntity<?> response = controller.rejectRequest(requestId);

        assertThat(response.getBody()).isInstanceOf(SuccessResponse.class);
        verify(friendRequestService).rejectRequest(requestId);
    }

    @Test
    void getPendingRequests_ReturnsSuccessResponse() {
        String username = "user2";
        Long userId = 3L;
        List<FriendRequestDto> requestList = List.of(FriendRequestDto.builder().build());

        when(userDetails.getUsername()).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(User.builder().build());
        when(friendRequestService.getPendingRequests(userId)).thenReturn(requestList);

        ResponseEntity<?> response = controller.getPendingRequests(userDetails);

        assertThat(response.getBody()).isInstanceOf(SuccessResponse.class);
        verify(friendRequestService).getPendingRequests(userId);
    }

    @Test
    void getSentRequests_ReturnsSuccessResponse() {
        String username = "user3";
        Long userId = 4L;
        List<FriendRequestDto> requestList = List.of(FriendRequestDto.builder().build());

        when(userDetails.getUsername()).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(User.builder().build());
        when(friendRequestService.getSentRequests(userId)).thenReturn(requestList);

        ResponseEntity<?> response = controller.getSentRequests(userDetails);

        assertThat(response.getBody()).isInstanceOf(SuccessResponse.class);
        verify(friendRequestService).getSentRequests(userId);
    }

    @Test
    void cancelRequest_ReturnsSuccessResponse() {
        Long requestId = 5L;

        ResponseEntity<?> response = controller.cancelRequest(requestId);

        assertThat(response.getBody()).isInstanceOf(SuccessResponse.class);
        verify(friendRequestService).cancelRequest(requestId);
    }
}

