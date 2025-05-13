package ru.senla.javacourse.mutovin.messenger.impl.service;

import ru.senla.javacourse.mutovin.messenger.api.dto.FriendRequestDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.FriendRequest;

import java.util.List;

public interface FriendRequestService {
    FriendRequestDto sendRequest(Long senderId, Long receiverId);
    FriendRequestDto acceptRequest(Long requestId);
    FriendRequestDto rejectRequest(Long requestId);
    List<FriendRequestDto> getPendingRequests(Long userId);
    List<FriendRequestDto> getSentRequests(Long userId);
    void cancelRequest(Long requestId);
    FriendRequest findById(Long id);
} 