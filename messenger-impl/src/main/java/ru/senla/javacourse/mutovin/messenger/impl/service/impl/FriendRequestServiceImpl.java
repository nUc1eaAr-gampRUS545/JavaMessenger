package ru.senla.javacourse.mutovin.messenger.impl.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.javacourse.mutovin.messenger.api.dto.FriendRequestDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.FriendRequest;
import ru.senla.javacourse.mutovin.messenger.db.entity.FriendRequestStatus;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;
import ru.senla.javacourse.mutovin.messenger.impl.exception.ResourceNotFoundException;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.FriendRequestMapper;
import ru.senla.javacourse.mutovin.messenger.impl.repository.FriendRequestRepository;
import ru.senla.javacourse.mutovin.messenger.impl.service.FriendRequestService;
import ru.senla.javacourse.mutovin.messenger.impl.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserService userService;
    private final FriendRequestMapper friendRequestMapper;

    @Override
    @Transactional
    public FriendRequestDto sendRequest(Long senderId,Long receiverId) {
        User sender = userService.findById(senderId);
        User recipient = userService.findById(receiverId);

        if (friendRequestRepository.findBySenderAndReceiver(sender.getUsername(),recipient.getUsername()).isPresent())
            throw new IllegalStateException("Запрос в друзья уже существует");

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setRecipient(recipient);
        request.setStatus(FriendRequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        FriendRequest result = friendRequestRepository.save(request).orElseThrow(
                () -> new ResourceNotFoundException("Запрос в друзья не найден")
        );
        return friendRequestMapper.map(result);
    }

    @Override
    @Transactional
    public FriendRequestDto acceptRequest(Long requestId) {
        FriendRequest request = findById(requestId);
        request.setStatus(FriendRequestStatus.ACCEPTED);
        request.setUpdatedAt(LocalDateTime.now());

        // Создаем запись о дружбе для обоих пользователей
        userService.addFriend(request.getSender().getId(),request.getRecipient().getId());
        userService.addFriend(request.getRecipient().getId(),request.getSender().getId());

        FriendRequest result = friendRequestRepository.save(request).orElseThrow(
                () -> new ResourceNotFoundException("Запрос в друзья не найден")
        );
        return friendRequestMapper.map(result);
    }

    @Override
    @Transactional
    public FriendRequestDto rejectRequest(Long requestId) {
        FriendRequest request = findById(requestId);
        request.setStatus(FriendRequestStatus.REJECTED);
        request.setUpdatedAt(LocalDateTime.now());
        FriendRequest result = friendRequestRepository.save(request).orElseThrow(
                () -> new ResourceNotFoundException("Запрос в друзья не найден")
        );
        return friendRequestMapper.map(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendRequestDto> getPendingRequests(Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findByReceiver(userId).orElseThrow(
                () -> new IllegalArgumentException("Что то пошло не так")
        );
        return requests.stream().filter(request -> FriendRequestStatus.PENDING.equals(request.getStatus()))
                .map(friendRequestMapper::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendRequestDto> getSentRequests(Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findBySender(userId).orElseThrow(
                () -> new IllegalArgumentException("Что то пошло не так")
        );
        return requests.stream().map(friendRequestMapper::map).toList();
    }

    @Override
    @Transactional
    public void cancelRequest(Long requestId) {
        FriendRequest request = findById(requestId);
        if (!FriendRequestStatus.PENDING.equals(request.getStatus())) {
            throw new IllegalStateException("Можно отменить только ожидающие запросы");
        }
        friendRequestRepository.deleteById(requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public FriendRequest findById(Long id) {
        return friendRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Запрос в друзья не найден с id: " + id));
    }
} 