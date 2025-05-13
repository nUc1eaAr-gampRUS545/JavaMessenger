package ru.senla.javacourse.mutovin.messenger.impl.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.javacourse.mutovin.messenger.api.dto.MessageDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.Chat;
import ru.senla.javacourse.mutovin.messenger.db.entity.Message;
import ru.senla.javacourse.mutovin.messenger.db.entity.MessageStatus;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;
import ru.senla.javacourse.mutovin.messenger.impl.exception.ChatException;
import ru.senla.javacourse.mutovin.messenger.impl.exception.MessageException;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.MessageMapper;
import ru.senla.javacourse.mutovin.messenger.impl.repository.ChatRepository;
import ru.senla.javacourse.mutovin.messenger.impl.repository.MessageRepository;
import ru.senla.javacourse.mutovin.messenger.impl.repository.UserRepository;
import ru.senla.javacourse.mutovin.messenger.impl.repository.ChatParticipantRepository;
import ru.senla.javacourse.mutovin.messenger.impl.service.MessageService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final MessageMapper messageMapper;

    @Override
    @Transactional
    public MessageDto createMessage(Long chatId, Long senderId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new MessageException.EmptyMessageContentException();
        }

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException.ChatNotFoundException(chatId));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + senderId));

        if (!chatParticipantRepository.findByChatIdAndUserId(chatId, senderId).isPresent()) {
            throw new MessageException.UserNotInChatException(senderId, chatId);
        }

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(content.trim());
        message.setStatus(MessageStatus.SENT);
        message.setCreatedAt(LocalDateTime.now());

        Message result = messageRepository.save(message)
                .orElseThrow(() -> new MessageException("Failed to save message"));
        return messageMapper.map(result);
    }

    @Override
    @Transactional
    public MessageDto updateMessage(Long messageId, String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new MessageException.EmptyMessageContentException();
        }

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException.MessageNotFoundException(messageId));

        if (message.getStatus() == MessageStatus.DELETED) {
            throw new MessageException.MessageNotEditableException(messageId);
        }

        message.setContent(newContent.trim());
        message.setStatus(MessageStatus.EDITED);
        message.setUpdatedAt(LocalDateTime.now());

        Message result = messageRepository.update(message)
                .orElseThrow(() -> new MessageException("Failed to update message"));
        return  messageMapper.map(result);
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException.MessageNotFoundException(messageId));

        if (message.getStatus() == MessageStatus.DELETED)
            throw new MessageException.MessageNotDeletableException(messageId);

        message.setStatus(MessageStatus.DELETED);
        message.setUpdatedAt(LocalDateTime.now());

        messageRepository.update(message)
                .orElseThrow(() -> new MessageException("Failed to delete message"));
    }

    @Override
    @Transactional
    public MessageDto markMessageAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException.MessageNotFoundException(messageId));

        if (message.getStatus() == MessageStatus.DELETED)
            throw new MessageException.MessageNotEditableException(messageId);

        message.setStatus(MessageStatus.READ);
        message.setUpdatedAt(LocalDateTime.now());

        Message result = messageRepository.update(message)
                .orElseThrow(() -> new MessageException("Failed to mark message as read"));
        return  messageMapper.map(result);
    }

    @Override
    @Transactional
    public MessageDto markMessageAsDelivered(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException.MessageNotFoundException(messageId));

        if (message.getStatus() == MessageStatus.DELETED)
            throw new MessageException.MessageNotEditableException(messageId);

        message.setStatus(MessageStatus.DELIVERED);
        message.setUpdatedAt(LocalDateTime.now());

        Message result = messageRepository.update(message)
                .orElseThrow(() -> new MessageException("Failed to mark message as delivered"));
        return  messageMapper.map(result);
    }

    @Override
    @Transactional
    public List<MessageDto> getChatMessages(Long chatId) {
        if (!chatRepository.existsById(chatId))
            throw new ChatException.ChatNotFoundException(chatId);

        List<Message> result = messageRepository.findByChatId(chatId)
                .orElseThrow(() -> new MessageException("Failed to get chat messages"));
        return result.stream().map(messageMapper :: map).toList();
    }

    @Override
    @Transactional
    public List<MessageDto> getUserMessages(Long userId) {
        if (!userRepository.existsById(userId))
            throw new IllegalArgumentException("User not found with id: " + userId);

        List<Message> result = messageRepository.findBySenderId(userId)
                .orElseThrow(() -> new MessageException("Failed to get user messages"));

        return result.stream().map(messageMapper :: map).toList();
    }

    @Override
    @Transactional
    public List<MessageDto> getUnreadMessages(Long userId) {
        if (!userRepository.existsById(userId))
            throw new IllegalArgumentException("User not found with id: " + userId);

        List<Message> result = messageRepository.findUnreadMessagesByUserId(userId)
                .orElseThrow(() -> new MessageException("Failed to get unread messages"));
        return result.stream().map(messageMapper :: map).toList();
    }

    @Override
    @Transactional
    public MessageDto getMessageById(Long messageId) {
        Message result = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException.MessageNotFoundException(messageId));
        return messageMapper.map(result);
    }

    @Override
    @Transactional
    public List<MessageDto> getMessagesByStatus(Long chatId, MessageStatus status) {
        if (!chatRepository.existsById(chatId))
            throw new ChatException.ChatNotFoundException(chatId);

        List<Message> result =  messageRepository.findByChatIdAndStatus(chatId, status)
                .orElseThrow(() -> new MessageException("Failed to get messages by status"));
        return result.stream().map(messageMapper :: map).toList();
    }

    @Override
    @Transactional
    public List<MessageDto> getMessagesByIds(Set<Long> messageIds) {
        List<Message> result =  messageRepository.findAllByMessageIds(messageIds)
                .orElseThrow(() -> new MessageException("Failed to get messages by ids"))
                .stream().toList();
        return result.stream().map(messageMapper :: map).toList();
    }
} 