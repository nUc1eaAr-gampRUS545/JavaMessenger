package ru.senla.javacourse.mutovin.messenger.impl.service;

import ru.senla.javacourse.mutovin.messenger.api.dto.ChatDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.Chat;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatService {

    ChatDto createChat(String name,Long creatorId,Set<Long> participantIds);
    ChatDto createPrivateChat(Long userId1,Long userId2);
    void deleteChat(Long chatId,Long userId);
    ChatDto addParticipant(Long chatId,Long userId);
    ChatDto removeParticipant(Long chatId,Long userId);
    ChatDto makeAdmin(Long chatId,Long userId);
    ChatDto removeAdmin(Long chatId,Long userId);
    List<ChatDto> getUserChats(Long userId);
    List<ChatDto> getActiveChats(Long userId);
    ChatDto getChatById(Long chatId,Long userId);
    ChatDto getPrivateChat(Long userId1,Long userId2);
    List<ChatDto> getChatsByIds(List<Long> chatIds);
    boolean isUserInChat(Long userId,Long chatId);
    boolean isUserAdmin(Long userId,Long chatId);

} 