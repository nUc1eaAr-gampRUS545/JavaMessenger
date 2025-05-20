package ru.senla.javacourse.mutovin.messenger.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.senla.javacourse.mutovin.messenger.api.dto.ChatDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.Chat;
import ru.senla.javacourse.mutovin.messenger.db.entity.ChatParticipant;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.ChatMapper;
import ru.senla.javacourse.mutovin.messenger.impl.repository.ChatParticipantRepository;
import ru.senla.javacourse.mutovin.messenger.impl.repository.ChatRepository;
import ru.senla.javacourse.mutovin.messenger.impl.repository.UserRepository;
import ru.senla.javacourse.mutovin.messenger.impl.service.impl.ChatServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatServiceImplTest {

    @Mock
    private ChatRepository chatRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatParticipantRepository chatParticipantRepository;
    @Mock
    private ChatMapper chatMapper;

    @InjectMocks
    private ChatServiceImpl chatService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void createChat_ShouldCreateChatSuccessfully() {
        Long creatorId = 1L;
        Set<Long> participantIds = Set.of(2L, 3L);
        User creator = new User(); creator.setId(creatorId);
        Chat chat = new Chat(); chat.setId(10L); chat.setName("Test Chat");
        ChatDto chatDto = new ChatDto();

        when(chatRepository.existsByName(anyString())).thenReturn(false);
        when(userRepository.findById(creatorId)).thenReturn(Optional.of(creator));
        when(chatRepository.save(any())).thenReturn(Optional.of(chat));
        when(chatParticipantRepository.save(any())).thenReturn(Optional.of(new ChatParticipant()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User()));
        when(chatMapper.map(chat)).thenReturn(chatDto);

        ChatDto result = chatService.createChat("Test Chat", creatorId, participantIds);

        assertEquals(chatDto, result);
        verify(chatRepository).save(any(Chat.class));
        verify(chatParticipantRepository, times(3)).save(any());
    }

    @Test
    void createPrivateChat_ShouldCreateSuccessfully() {
        Long user1Id = 1L, user2Id = 2L;
        User user1 = new User(); user1.setId(user1Id); user1.setUsername("Alice");
        User user2 = new User(); user2.setId(user2Id); user2.setUsername("Bob");
        Chat chat = new Chat(); chat.setId(100L); chat.setCreator(user1);
        ChatDto chatDto = new ChatDto();

        when(chatRepository.findPrivateChatByParticipants(user1Id, user2Id)).thenReturn(Optional.empty());
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));
        when(userRepository.findById(user2Id)).thenReturn(Optional.of(user2));
        when(chatParticipantRepository.save(any())).thenReturn(Optional.of(new ChatParticipant()));
        when(chatMapper.map(chat)).thenReturn(chatDto);

        ChatDto result = chatService.createPrivateChat(user1Id, user2Id);

        assertEquals(chatDto, result);
    }

    @Test
    void deleteChat_ShouldDeletePrivateChatByCreator() {
        Long chatId = 1L, userId = 10L;
        Chat chat = new Chat(); chat.setId(chatId); chat.setIsPrivate(true);
        User user = new User(); user.setId(userId);
        chat.setParticipants(Set.of());

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));

        chatService.deleteChat(chatId, userId);

        verify(chatRepository).deleteById(chatId);
    }

    @Test
    void addParticipant_ShouldAddParticipantToChat() {
        Long chatId = 1L, userId = 2L;
        Chat chat = new Chat(); chat.setId(chatId);
        User user = new User(); user.setId(userId);
        ChatDto chatDto = new ChatDto();

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(chatParticipantRepository.findByChatIdAndUserId(chatId, userId)).thenReturn(Optional.empty());
        when(chatParticipantRepository.save(any())).thenReturn(Optional.of(new ChatParticipant()));
        when(chatMapper.map(chat)).thenReturn(chatDto);

        ChatDto result = chatService.addParticipant(chatId, userId);

        assertEquals(chatDto, result);
    }

    @Test
    void getPrivateChat_ShouldReturnChatDtoIfExists() {
        Long userId1 = 1L, userId2 = 2L;
        Chat chat = new Chat();
        ChatDto dto = new ChatDto();

        when(chatRepository.findPrivateChatByParticipants(userId1, userId2)).thenReturn(Optional.of(chat));
        when(chatMapper.map(chat)).thenReturn(dto);

        ChatDto result = chatService.getPrivateChat(userId1, userId2);

        assertEquals(dto, result);
    }

    @Test
    void getChatsByIds_ShouldReturnMappedChats() {
        List<Long> ids = List.of(1L, 2L);
        List<Chat> chats = List.of(new Chat(), new Chat());
        ChatDto dto = new ChatDto();

        when(chatMapper.map(any(Chat.class))).thenReturn(dto);

        List<ChatDto> result = chatService.getChatsByIds(ids);

        assertEquals(2, result.size());
        verify(chatRepository).findAllByChatIds(ids);
    }

    @Test
    void getChatById_ShouldReturnChatDto() {
        Long chatId = 1L;
        Long userId = 2L;
        Chat chat = new Chat();
        ChatDto dto = new ChatDto();

        when(chatRepository.findChatById(chatId, userId)).thenReturn(Optional.of(chat));
        when(chatMapper.map(chat)).thenReturn(dto);

        ChatDto result = chatService.getChatById(chatId, userId);

        assertEquals(dto, result);
    }

    @Test
    void getActiveChats_ShouldReturnActiveChats() {
        Long userId = 1L;
        Chat chat1 = new Chat();
        Chat chat2 = new Chat();
        List<Chat> chats = List.of(chat1, chat2);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(chatRepository.findActiveChatsByUserId(userId)).thenReturn(Optional.of(chats));
        when(chatMapper.map(any(Chat.class))).thenReturn(new ChatDto());

        List<ChatDto> result = chatService.getActiveChats(userId);

        assertEquals(2, result.size());
    }

    @Test
    void isUserAdmin_ShouldReturnTrueIfAdmin() {
        Long chatId = 1L;
        Long userId = 2L;

        when(chatParticipantRepository.isUserAdminInChat(userId, chatId)).thenReturn(true);

        boolean result = chatService.isUserAdmin(userId, chatId);

        assertTrue(result);
    }
}

