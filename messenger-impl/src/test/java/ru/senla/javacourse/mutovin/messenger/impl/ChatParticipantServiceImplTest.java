package ru.senla.javacourse.mutovin.messenger.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.senla.javacourse.mutovin.messenger.api.dto.ChatParticipantDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.ChatParticipant;
import ru.senla.javacourse.mutovin.messenger.impl.exception.ChatParticipantException;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.ChatParticipantMapper;
import ru.senla.javacourse.mutovin.messenger.impl.repository.ChatParticipantRepository;
import ru.senla.javacourse.mutovin.messenger.impl.service.impl.ChatParticipantServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatParticipantServiceImplTest {

    @InjectMocks
    private ChatParticipantServiceImpl chatParticipantService;

    @Mock
    private ChatParticipantRepository chatParticipantRepository;

    @Mock
    private ChatParticipantMapper chatParticipantMapper;

    @Mock
    private ChatParticipant chatParticipant;

    @Mock
    private ChatParticipantDto chatParticipantDto;

    private final Long chatId = 1L;
    private final Long userId = 2L;
    private final Long participantId = 3L;
    private final Set<Long> participantIds = Set.of(1L, 2L, 3L);

    @Test
    void findByChatId_success() {
        List<ChatParticipant> participants = List.of(chatParticipant);
        when(chatParticipantRepository.findByChatId(chatId)).thenReturn(Optional.of(participants));
        when(chatParticipantMapper.map(any())).thenReturn(chatParticipantDto);

        List<ChatParticipantDto> result = chatParticipantService.findByChatId(chatId);

        assertEquals(1, result.size());
        verify(chatParticipantRepository).findByChatId(chatId);
    }

    @Test
    void findByChatId_notFound() {
        when(chatParticipantRepository.findByChatId(chatId)).thenReturn(Optional.empty());
        assertThrows(ChatParticipantException.class, () -> chatParticipantService.findByChatId(chatId));
    }

    @Test
    void findByUserId_success() {
        List<ChatParticipant> participants = List.of(chatParticipant);
        when(chatParticipantRepository.findByUserId(userId)).thenReturn(Optional.of(participants));
        when(chatParticipantMapper.map(any())).thenReturn(chatParticipantDto);

        List<ChatParticipantDto> result = chatParticipantService.findByUserId(userId);

        assertEquals(1, result.size());
        verify(chatParticipantRepository).findByUserId(userId);
    }

    @Test
    void findByChatIdAndLeftAtIsNull_success() {
        List<ChatParticipant> participants = List.of(chatParticipant);
        when(chatParticipantRepository.findActiveParticipantsByChatId(chatId)).thenReturn(Optional.of(participants));
        when(chatParticipantMapper.map(any())).thenReturn(chatParticipantDto);

        List<ChatParticipantDto> result = chatParticipantService.findByChatIdAndLeftAtIsNull(chatId);

        assertEquals(1, result.size());
        verify(chatParticipantRepository).findActiveParticipantsByChatId(chatId);
    }

    @Test
    void findByChatIdAndUserId_success() {
        when(chatParticipantRepository.findByChatIdAndUserId(chatId, userId)).thenReturn(Optional.of(chatParticipant));
        when(chatParticipantMapper.map(chatParticipant)).thenReturn(chatParticipantDto);

        ChatParticipantDto result = chatParticipantService.findByChatIdAndUserId(chatId, userId);

        assertNotNull(result);
        verify(chatParticipantRepository).findByChatIdAndUserId(chatId, userId);
    }

    @Test
    void findByChatIdAndUserId_notFound() {
        when(chatParticipantRepository.findByChatIdAndUserId(chatId, userId)).thenReturn(Optional.empty());
        assertThrows(ChatParticipantException.ChatParticipantNotFoundException.class,
                () -> chatParticipantService.findByChatIdAndUserId(chatId, userId));
    }

    @Test
    void getChatParticipants_delegatesToFindByChatId() {
        ChatParticipantServiceImpl spyService = Mockito.spy(chatParticipantService);
        doReturn(List.of(chatParticipantDto)).when(spyService).findByChatId(chatId);

        List<ChatParticipantDto> result = spyService.getChatParticipants(chatId);

        assertEquals(1, result.size());
        verify(spyService).findByChatId(chatId);
    }

    @Test
    void getUserParticipations_delegatesToFindByUserId() {
        ChatParticipantServiceImpl spyService = Mockito.spy(chatParticipantService);
        doReturn(List.of(chatParticipantDto)).when(spyService).findByUserId(userId);

        List<ChatParticipantDto> result = spyService.getUserParticipations(userId);

        assertEquals(1, result.size());
        verify(spyService).findByUserId(userId);
    }

    @Test
    void getActiveChatParticipants_delegatesToFindByChatIdAndLeftAtIsNull() {
        ChatParticipantServiceImpl spyService = Mockito.spy(chatParticipantService);
        doReturn(List.of(chatParticipantDto)).when(spyService).findByChatIdAndLeftAtIsNull(chatId);

        List<ChatParticipantDto> result = spyService.getActiveChatParticipants(chatId);

        assertEquals(1, result.size());
        verify(spyService).findByChatIdAndLeftAtIsNull(chatId);
    }

    @Test
    void getParticipantById_success() {
        when(chatParticipantRepository.findById(participantId)).thenReturn(Optional.of(chatParticipant));
        when(chatParticipantMapper.map(chatParticipant)).thenReturn(chatParticipantDto);

        ChatParticipantDto result = chatParticipantService.getParticipantById(participantId);

        assertNotNull(result);
        verify(chatParticipantRepository).findById(participantId);
    }

    @Test
    void getParticipantById_notFound() {
        when(chatParticipantRepository.findById(participantId)).thenReturn(Optional.empty());
        assertThrows(ChatParticipantException.ChatParticipantNotFoundException.class,
                () -> chatParticipantService.getParticipantById(participantId));
    }

    @Test
    void getParticipantByChatAndUser_delegatesToFindByChatIdAndUserId() {
        ChatParticipantServiceImpl spyService = Mockito.spy(chatParticipantService);
        doReturn(chatParticipantDto).when(spyService).findByChatIdAndUserId(chatId, userId);

        ChatParticipantDto result = spyService.getParticipantByChatAndUser(chatId, userId);

        assertNotNull(result);
        verify(spyService).findByChatIdAndUserId(chatId, userId);
    }
}

