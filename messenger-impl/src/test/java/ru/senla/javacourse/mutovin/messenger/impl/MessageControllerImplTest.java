package ru.senla.javacourse.mutovin.messenger.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import ru.senla.javacourse.mutovin.messenger.api.dto.MessageDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.MessageCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.MessageUpdateRequest;
import ru.senla.javacourse.mutovin.messenger.db.entity.MessageStatus;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;
import ru.senla.javacourse.mutovin.messenger.impl.controller.MessageControllerImpl;
import ru.senla.javacourse.mutovin.messenger.impl.service.MessageService;
import ru.senla.javacourse.mutovin.messenger.impl.service.UserService;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MessageControllerImplTest {

    @InjectMocks
    private MessageControllerImpl messageController;

    @Mock
    private MessageService messageService;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createMessage_shouldReturnSuccessResponse() {
        MessageCreateRequest request = MessageCreateRequest.builder().chatId(1L).content("Hi").build();
        User user = new User();
        user.setId(10L);

        MessageDto messageDto = MessageDto.builder().build();
        messageDto.setId(123L);

        when(userDetails.getUsername()).thenReturn("user");
        when(userService.findByUsername("user")).thenReturn(user);
        when(messageService.createMessage(1L, 10L, "Hello")).thenReturn(messageDto);

        ResponseEntity<?> response = messageController.createMessage(userDetails, request);

        assertEquals(200, response.getStatusCodeValue());
        verify(messageService).createMessage(1L, 10L, "Hello");
    }

    @Test
    void updateMessage_shouldReturnUpdatedMessage() {
        Long messageId = 1L;
        MessageUpdateRequest request = MessageUpdateRequest.builder().content("Updated text").build();
        MessageDto updatedDto = MessageDto.builder().build();
        updatedDto.setId(messageId);

        when(messageService.updateMessage(messageId, "Updated text")).thenReturn(updatedDto);

        ResponseEntity<?> response = messageController.updateMessage(messageId, request);

        assertEquals(200, response.getStatusCodeValue());
        verify(messageService).updateMessage(messageId, "Updated text");
    }

    @Test
    void deleteMessage_shouldReturnSuccessResponse() {
        Long messageId = 5L;

        ResponseEntity<?> response = messageController.deleteMessage(messageId);

        assertEquals(200, response.getStatusCodeValue());
        verify(messageService).deleteMessage(messageId);
    }

    @Test
    void markMessageAsRead_shouldReturnSuccessResponse() {
        Long messageId = 7L;
        MessageDto messageDto = MessageDto.builder().build();
        messageDto.setId(messageId);

        when(messageService.markMessageAsRead(messageId)).thenReturn(messageDto);

        ResponseEntity<?> response = messageController.markMessageAsRead(messageId);

        assertEquals(200, response.getStatusCodeValue());
        verify(messageService).markMessageAsRead(messageId);
    }

    @Test
    void markMessageAsDelivered_shouldReturnSuccessResponse() {
        Long messageId = 9L;
        MessageDto messageDto = MessageDto.builder().build();
        messageDto.setId(messageId);

        when(messageService.markMessageAsDelivered(messageId)).thenReturn(messageDto);

        ResponseEntity<?> response = messageController.markMessageAsDelivered(messageId);

        assertEquals(200, response.getStatusCodeValue());
        verify(messageService).markMessageAsDelivered(messageId);
    }

    @Test
    void getChatMessages_shouldReturnListOfMessages() {
        Long chatId = 1L;
        List<MessageDto> messages = List.of( MessageDto.builder().build(),  MessageDto.builder().build());

        when(messageService.getChatMessages(chatId)).thenReturn(messages);

        ResponseEntity<?> response = messageController.getChatMessages(chatId);

        assertEquals(200, response.getStatusCodeValue());
        verify(messageService).getChatMessages(chatId);
    }

    @Test
    void getUserMessages_shouldReturnListOfMessages() {
        Long userId = 2L;
        List<MessageDto> messages = List.of( MessageDto.builder().build());

        when(messageService.getUserMessages(userId)).thenReturn(messages);

        ResponseEntity<?> response = messageController.getUserMessages(userId);

        assertEquals(200, response.getStatusCodeValue());
        verify(messageService).getUserMessages(userId);
    }

    @Test
    void getUnreadMessages_shouldReturnListOfUnreadMessages() {
        Long userId = 3L;
        List<MessageDto> messages = List.of( MessageDto.builder().build());

        when(messageService.getUnreadMessages(userId)).thenReturn(messages);

        ResponseEntity<?> response = messageController.getUnreadMessages(userId);

        assertEquals(200, response.getStatusCodeValue());
        verify(messageService).getUnreadMessages(userId);
    }

    @Test
    void getMessageById_shouldReturnMessage() {
        Long messageId = 4L;
        MessageDto messageDto = MessageDto.builder().build();
        messageDto.setId(messageId);

        when(messageService.getMessageById(messageId)).thenReturn(messageDto);

        ResponseEntity<?> response = messageController.getMessageById(messageId);

        assertEquals(200, response.getStatusCodeValue());
        verify(messageService).getMessageById(messageId);
    }

    @Test
    void getMessagesByStatus_shouldReturnMessagesWithStatus() {
        Long chatId = 1L;
        MessageStatus status = MessageStatus.SENT;
        List<MessageDto> messages = List.of( MessageDto.builder().build());

        when(messageService.getMessagesByStatus(chatId, status)).thenReturn(messages);

        ResponseEntity<?> response = messageController.getMessagesByStatus(chatId, status);

        assertEquals(200, response.getStatusCodeValue());
        verify(messageService).getMessagesByStatus(chatId, status);
    }

    @Test
    void getMessagesByIds_shouldReturnListOfMessages() {
        Set<Long> ids = Set.of(1L, 2L, 3L);
        List<MessageDto> messages = List.of(MessageDto.builder().build());

        when(messageService.getMessagesByIds(ids)).thenReturn(messages);

        ResponseEntity<?> response = messageController.getMessagesByIds(ids);

        assertEquals(200, response.getStatusCodeValue());
        verify(messageService).getMessagesByIds(ids);
    }
}

