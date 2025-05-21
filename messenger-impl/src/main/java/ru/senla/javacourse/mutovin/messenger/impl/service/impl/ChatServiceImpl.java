package ru.senla.javacourse.mutovin.messenger.impl.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.javacourse.mutovin.messenger.api.dto.ChatDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.Chat;
import ru.senla.javacourse.mutovin.messenger.db.entity.ChatParticipant;
import ru.senla.javacourse.mutovin.messenger.db.entity.Message;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;
import ru.senla.javacourse.mutovin.messenger.impl.exception.ChatException;
import ru.senla.javacourse.mutovin.messenger.impl.exception.MessageException;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.ChatMapper;
import ru.senla.javacourse.mutovin.messenger.impl.repository.ChatParticipantRepository;
import ru.senla.javacourse.mutovin.messenger.impl.repository.ChatRepository;
import ru.senla.javacourse.mutovin.messenger.impl.repository.MessageRepository;
import ru.senla.javacourse.mutovin.messenger.impl.repository.UserRepository;
import ru.senla.javacourse.mutovin.messenger.impl.service.ChatService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMapper chatMapper;

    @Override
    @Transactional
    public ChatDto createChat(String name,Long creatorId,Set<Long> participantIds) {
        if (name==null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Chat name cannot be empty");
        }

        if (chatRepository.existsByName(name)) {
            throw new ChatException.ChatNameAlreadyExistsException(name);
        }

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + creatorId));

        Chat chat = new Chat();
        chat.setName(name.trim());
        chat.setIsPrivate(false);
        chat.setCreator(creator);
        chat.setCreatedAt(LocalDateTime.now());

        Chat savedChat = chatRepository.save(chat)
                .orElseThrow(() -> new ChatException("Failed to create chat"));

        ChatParticipant creatorParticipant = new ChatParticipant();
        creatorParticipant.setChat(savedChat);
        creatorParticipant.setUser(creator);
        creatorParticipant.setIsAdmin(true);
        creatorParticipant.setJoinedAt(LocalDateTime.now());


        chatParticipantRepository.save(creatorParticipant)
                .orElseThrow(() -> new ChatException("Failed to add creator to chat"));

        participantIds.forEach((userId) -> {
            if (!Objects.equals(creatorId,userId)) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + creatorId));
                ChatParticipant participant = new ChatParticipant();
                participant.setChat(savedChat);
                participant.setUser(user);
                participant.setIsAdmin(false);
                participant.setJoinedAt(LocalDateTime.now());
                chatParticipantRepository.save(participant)
                        .orElseThrow(() -> new ChatException("Failed to add user to chat"));
            }

        });

        return chatMapper.map(savedChat);
    }

    @Override
    @Transactional
    public ChatDto createPrivateChat(Long userId1,Long userId2) {
        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("Cannot create private chat with the same user");
        }

        Optional<Chat> existingChat = chatRepository.findPrivateChatByParticipants(userId1,userId2);
        if (existingChat.isPresent()) {
            throw new ChatException.PrivateChatAlreadyExistsException(userId1,userId2);
        }

        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId1));

        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId2));

        Chat chat = new Chat();
        chat.setName("Приветнаый чат: " + user1.getUsername() + " & " + user2.getUsername());
        chat.setIsPrivate(true);
        chat.setCreator(user1);
        chat.setCreatedAt(LocalDateTime.now());

        Chat savedChat = chatRepository.save(chat)
                .orElseThrow(() -> new ChatException("Failed to create private chat"));

        addParticipant(savedChat.getId(),userId1);
        addParticipant(savedChat.getId(),userId2);

        return chatMapper.map(savedChat);
    }

    @Override
    @Transactional
    public void deleteChat(Long chatId,Long userId) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException.ChatNotFoundException(chatId));

        chat.getParticipants().forEach(
                user -> {
                    if (Objects.equals(user.getId(),userId) && chat.getIsPrivate())
                        chatRepository.deleteById(chatId);});
    }
    @Override
    @Transactional
    public ChatDto addParticipant(Long chatId,Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException.ChatNotFoundException(chatId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (chatParticipantRepository.findByChatIdAndUserId(chatId,userId).isPresent()) {
            throw new ChatException("User is already a participant in this chat");
        }

//        if(chat.getIsPrivate()){
//            throw new ChatException.UserAccessDeniedException(chatId,userId);
//        }

        ChatParticipant participant = new ChatParticipant();
        participant.setChat(chat);
        participant.setUser(user);
        participant.setIsAdmin(false);
        participant.setJoinedAt(LocalDateTime.now());

        chatParticipantRepository.save(participant)
                .orElseThrow(() -> new ChatException("Failed to add participant to chat"));

        return chatMapper.map(chat);
    }

    @Override
    @Transactional
    public void removeParticipant(Long chatId,Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException.ChatNotFoundException(chatId));

        ChatParticipant participant = chatParticipantRepository.findByChatIdAndUserId(chatId,userId)
                .orElseThrow(() -> new ChatException.UserNotInChatException(userId,chatId));

        participant.setLeftAt(LocalDateTime.now());
        chatParticipantRepository.deleteById(participant.getId());

    }

    @Override
    @Transactional
    public ChatDto makeAdmin(Long chatId,Long userId) {
        if (!isUserAdmin(chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException.ChatNotFoundException(chatId))
                .getCreator().getId(),chatId)) {
            throw new ChatException.UserNotAdminException(userId,chatId);
        }

        ChatParticipant participant = chatParticipantRepository.findByChatIdAndUserId(chatId,userId)
                .orElseThrow(() -> new ChatException.UserNotInChatException(userId,chatId));

        participant.setIsAdmin(true);
        chatParticipantRepository.update(participant)
                .orElseThrow(() -> new ChatException("Failed to make user admin"));

        Chat dto = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException.ChatNotFoundException(chatId));
        return chatMapper.map(dto);
    }

    @Override
    @Transactional
    public ChatDto removeAdmin(Long chatId,Long userId) {
        if (!isUserAdmin(chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException.ChatNotFoundException(chatId)).getCreator().getId(),chatId))
            throw new ChatException.UserNotAdminException(userId,chatId);


        ChatParticipant participant = chatParticipantRepository.findByChatIdAndUserId(chatId,userId)
                .orElseThrow(() -> new ChatException.UserNotInChatException(userId,chatId));

        participant.setIsAdmin(false);
        chatParticipantRepository.update(participant)
                .orElseThrow(() -> new ChatException("Failed to remove admin rights"));

        Chat dto = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException.ChatNotFoundException(chatId));

        return chatMapper.map(dto);
    }

    @Override
    @Transactional
    public List<ChatDto> getUserChats(Long userId) {
        if (!userRepository.existsById(userId))
            throw new IllegalArgumentException("User not found with id: " + userId);

        List<Chat> chatList = chatRepository.findByParticipantId(userId)
                .orElseThrow(() -> new ChatException("Failed to get user chats"));

        return chatList.stream().map(chatMapper::map).toList();
    }

    @Override
    @Transactional
    public List<ChatDto> getActiveChats(Long userId) {
        if (!userRepository.existsById(userId))
            throw new IllegalArgumentException("User not found with id: " + userId);

        List<Chat> chatList = chatRepository.findActiveChatsByUserId(userId)
                .orElseThrow(() -> new ChatException("Failed to get active chats"));

        return chatList.stream().map(chatMapper::map).toList();
    }

    @Override
    public ChatDto getChatById(Long chatId, Long userId) {
        Chat chat = chatRepository.findChatById(chatId, userId).orElseThrow(
                () -> new ChatException.ChatNotFoundException(chatId)
        );
        return chatMapper.map(chat);
    }

    @Override
    @Transactional
    public ChatDto getPrivateChat(Long userId1,Long userId2) {
        Chat chat = chatRepository.findPrivateChatByParticipants(userId1,userId2).orElseThrow(
                () -> new ChatException.UserNotInChatException(userId1,userId2)
        );
        return chatMapper.map(chat);
    }

    @Override
    @Transactional
    public List<ChatDto> getChatsByIds(List<Long> chatIds) {
        List<Chat> chats = chatRepository.findAllByChatIds(chatIds)
                .orElseThrow(() -> new ChatException("Failed to get chats by ids"))
                .stream().toList();

        return chats.stream().map(chatMapper::map).toList();
    }

    @Override
    @Transactional
    public boolean isUserInChat(Long userId,Long chatId) {
        return chatParticipantRepository.findByChatIdAndUserId(chatId,userId).isPresent();
    }

    @Override
    @Transactional
    public boolean isUserAdmin(Long userId,Long chatId) {
        return chatParticipantRepository.isUserAdminInChat(userId,chatId);
    }
} 