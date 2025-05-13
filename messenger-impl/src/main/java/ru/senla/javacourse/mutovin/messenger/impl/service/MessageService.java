package ru.senla.javacourse.mutovin.messenger.impl.service;

import ru.senla.javacourse.mutovin.messenger.api.dto.MessageDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.Message;
import ru.senla.javacourse.mutovin.messenger.db.entity.MessageStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MessageService {
    MessageDto createMessage(Long chatId,Long senderId,String content);
    MessageDto updateMessage(Long messageId, String newContent);
    void deleteMessage(Long messageId);
    MessageDto markMessageAsRead(Long messageId);
    MessageDto markMessageAsDelivered(Long messageId);
    List<MessageDto> getChatMessages(Long chatId);
    List<MessageDto> getUserMessages(Long userId);
    List<MessageDto> getUnreadMessages(Long userId);
    MessageDto getMessageById(Long messageId);
    List<MessageDto> getMessagesByStatus(Long chatId, MessageStatus status);
    List<MessageDto> getMessagesByIds(Set<Long> messageIds);
} 