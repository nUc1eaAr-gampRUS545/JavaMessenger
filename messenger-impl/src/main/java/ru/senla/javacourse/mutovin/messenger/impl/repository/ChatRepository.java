package ru.senla.javacourse.mutovin.messenger.impl.repository;

import ru.senla.javacourse.mutovin.messenger.db.entity.Chat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatRepository extends GenericRepository<Chat, Long> {
    Optional<Chat> findChatById(Long id, Long userId);
    Optional<List<Chat>> findByParticipantId(Long participantId);
    Optional<Chat> findPrivateChatByParticipants(Long userId1, Long userId2);
    Optional<Chat> update(Chat chat);
    Optional<List<Chat>> findActiveChatsByUserId(Long userId);
    Optional<Set<Chat>> findAllByChatIds(List<Long> chatIds);
    boolean existsByName(String name);
} 