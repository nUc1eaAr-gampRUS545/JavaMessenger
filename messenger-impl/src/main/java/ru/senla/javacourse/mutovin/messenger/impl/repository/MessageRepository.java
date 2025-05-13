package ru.senla.javacourse.mutovin.messenger.impl.repository;

import ru.senla.javacourse.mutovin.messenger.db.entity.Message;
import ru.senla.javacourse.mutovin.messenger.db.entity.MessageStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MessageRepository extends GenericRepository<Message, Long> {
    Optional<List<Message>> findByChatId(Long chatId);
    Optional<List<Message>> findBySenderId(Long senderId);
    Optional<List<Message>> findByChatIdAndStatus(Long chatId, MessageStatus status);
    Optional<Message> update(Message message);
    Optional<List<Message>> findUnreadMessagesByUserId(Long userId);
    Optional<List<Message>> findMessagesByChatIdAndUserId(Long chatId, Long userId);
    Optional<Set<Message>> findAllByMessageIds(Set<Long> messageIds);
} 