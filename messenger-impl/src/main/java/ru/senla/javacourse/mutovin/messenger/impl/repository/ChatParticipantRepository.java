package ru.senla.javacourse.mutovin.messenger.impl.repository;

import ru.senla.javacourse.mutovin.messenger.db.entity.ChatParticipant;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatParticipantRepository extends GenericRepository<ChatParticipant, Long> {
    Optional<List<ChatParticipant>> findByChatId(Long chatId);
    Optional<List<ChatParticipant>> findByUserId(Long userId);
    Optional<ChatParticipant> findByChatIdAndUserId(Long chatId, Long userId);
    Optional<ChatParticipant> update(ChatParticipant participant);
    Optional<List<ChatParticipant>> findActiveParticipantsByChatId(Long chatId);
    Optional<Set<ChatParticipant>> findAllByParticipantIds(Set<Long> participantIds);
    boolean isUserAdminInChat(Long userId, Long chatId);
} 