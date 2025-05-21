package ru.senla.javacourse.mutovin.messenger.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.senla.javacourse.mutovin.messenger.api.dto.MessageDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.*;
import ru.senla.javacourse.mutovin.messenger.impl.exception.MessageException;
import ru.senla.javacourse.mutovin.messenger.impl.kafka.KafkaProducer;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.MessageMapper;
import ru.senla.javacourse.mutovin.messenger.impl.repository.*;
import ru.senla.javacourse.mutovin.messenger.impl.service.impl.MessageServiceImpl;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MessageServiceImplTest {

    @InjectMocks
    private MessageServiceImpl messageService;

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatParticipantRepository chatParticipantRepository;
    @Mock
    private KafkaProducer kafkaProducer;
    @Mock
    private MessageMapper messageMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createMessage_shouldCreateSuccessfully() {
        Long chatId = 1L;
        Long senderId = 2L;
        String content = "Hello";

        Chat chat = new Chat();
        chat.setId(chatId);
        User user = new User();
        user.setId(senderId);

        Message message = new Message();
        message.setContent(content);
        message.setChat(chat);
        message.setSender(user);
        message.setStatus(MessageStatus.SENT);
        message.setCreatedAt(LocalDateTime.now());

        Message savedMessage = new Message();
        savedMessage.setId(99L);

        MessageDto messageDto = MessageDto.builder().build();
        messageDto.setId(99L);

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(userRepository.findById(senderId)).thenReturn(Optional.of(user));
        when(chatParticipantRepository.findByChatIdAndUserId(chatId, senderId)).thenReturn(Optional.of(new ChatParticipant()));
        when(messageRepository.save(any(Message.class))).thenReturn(Optional.of(savedMessage));
        when(messageMapper.map(savedMessage)).thenReturn(messageDto);

        MessageDto result = messageService.createMessage(chatId, senderId, content);

        assertEquals(99L, result.getId());
        verify(kafkaProducer, never()).send(anyString(), anyString()); // sender is the only participant
    }

    @Test
    void updateMessage_shouldThrowExceptionIfEmptyContent() {
        assertThrows(MessageException.EmptyMessageContentException.class,
                () -> messageService.updateMessage(1L, "  "));
    }

    @Test
    void updateMessage_shouldUpdateSuccessfully() {
        Long messageId = 10L;
        String newContent = "Updated content";

        Message message = new Message();
        message.setId(messageId);
        message.setStatus(MessageStatus.SENT);
        message.setContent("Old content");

        Message updated = new Message();
        updated.setId(messageId);
        updated.setContent(newContent);
        updated.setStatus(MessageStatus.EDITED);

        MessageDto dto = MessageDto.builder().build();
        dto.setId(messageId);

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(messageRepository.update(any(Message.class))).thenReturn(Optional.of(updated));
        when(messageMapper.map(updated)).thenReturn(dto);

        MessageDto result = messageService.updateMessage(messageId, newContent);

        assertEquals(messageId, result.getId());
        verify(messageRepository).update(any(Message.class));
    }

    @Test
    void deleteMessage_shouldMarkAsDeleted() {
        Message message = new Message();
        message.setId(1L);
        message.setStatus(MessageStatus.SENT);

        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(messageRepository.update(any(Message.class))).thenReturn(Optional.of(message));

        assertDoesNotThrow(() -> messageService.deleteMessage(1L));
        assertEquals(MessageStatus.DELETED, message.getStatus());
    }

    @Test
    void getChatMessages_shouldReturnList() {
        Long chatId = 1L;

        Message message = new Message();
        message.setId(1L);
        List<Message> messages = List.of(message);

        MessageDto dto = MessageDto.builder().build();
        dto.setId(1L);

        when(chatRepository.existsById(chatId)).thenReturn(true);
        when(messageRepository.findByChatId(chatId)).thenReturn(Optional.of(messages));
        when(messageMapper.map(any(Message.class))).thenReturn(dto);

        List<MessageDto> result = messageService.getChatMessages(chatId);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getMessageById_shouldReturnMappedDto() {
        Long messageId = 5L;
        Message message = new Message();
        message.setId(messageId);
        MessageDto dto = MessageDto.builder().build();
        dto.setId(messageId);

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(messageMapper.map(message)).thenReturn(dto);

        MessageDto result = messageService.getMessageById(messageId);
        assertEquals(messageId, result.getId());
    }

    @Test
    void markMessageAsRead_shouldUpdateStatus() {
        Long messageId = 1L;
        Message message = new Message();
        message.setId(messageId);
        message.setStatus(MessageStatus.SENT);

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(messageRepository.update(any(Message.class))).thenReturn(Optional.of(message));

        assertDoesNotThrow(() -> messageService.markMessageAsRead(messageId));
        assertEquals(MessageStatus.READ, message.getStatus());
    }

    @Test
    void markMessageAsDelivered_shouldUpdateStatus() {
        Long messageId = 2L;
        Message message = new Message();
        message.setId(messageId);
        message.setStatus(MessageStatus.SENT);

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(messageRepository.update(any(Message.class))).thenReturn(Optional.of(message));

        assertDoesNotThrow(() -> messageService.markMessageAsDelivered(messageId));
        assertEquals(MessageStatus.DELIVERED, message.getStatus());
    }

    @Test
    void getMessagesByStatus_shouldReturnFilteredMessages() {
        Message message1 = new Message();
        message1.setId(1L);
        message1.setStatus(MessageStatus.READ);

        MessageDto dto = MessageDto.builder().build();
        dto.setId(1L);

        when(messageMapper.map(any(Message.class))).thenReturn(dto);

        List<MessageDto> result = messageService.getMessagesByStatus(1L,MessageStatus.READ);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getMessagesByIds_shouldReturnMappedList() {
        Set<Long> ids = Set.of(1L, 2L);

        Message message1 = new Message();
        message1.setId(1L);
        Message message2 = new Message();
        message2.setId(2L);

        MessageDto dto1 = MessageDto.builder().build();
        dto1.setId(1L);
        MessageDto dto2 = MessageDto.builder().build();
        dto2.setId(2L);

        when(messageMapper.map(message1)).thenReturn(dto1);
        when(messageMapper.map(message2)).thenReturn(dto2);

        List<MessageDto> result = messageService.getMessagesByIds(ids);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(1L)));
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(2L)));
    }

    @Test
    void getUserMessages_shouldReturnUserMessages() {
        Long userId = 3L;
        Message message = new Message();
        message.setId(1L);
        MessageDto dto = MessageDto.builder().build();
        dto.setId(1L);

        when(messageRepository.findBySenderId(userId)).thenReturn(Optional.of(List.of(message)));
        when(messageMapper.map(message)).thenReturn(dto);

        List<MessageDto> result = messageService.getUserMessages(userId);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getUnreadMessages_shouldReturnUnreadMessages() {
        Long userId = 4L;
        Message message = new Message();
        message.setId(1L);
        message.setStatus(MessageStatus.SENT);
        MessageDto dto = MessageDto.builder().build();
        dto.setId(1L);

        when(messageMapper.map(message)).thenReturn(dto);

        List<MessageDto> result = messageService.getUnreadMessages(userId);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
}
