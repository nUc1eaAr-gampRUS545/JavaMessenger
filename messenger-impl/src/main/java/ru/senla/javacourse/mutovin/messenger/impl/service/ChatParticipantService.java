package ru.senla.javacourse.mutovin.messenger.impl.service;

import ru.senla.javacourse.mutovin.messenger.api.dto.ChatParticipantDto;

import java.util.List;
import java.util.Set;

public interface ChatParticipantService {
    List<ChatParticipantDto> findByChatId(Long chatId);
    List<ChatParticipantDto> findByUserId(Long userId);
    List<ChatParticipantDto> findByChatIdAndLeftAtIsNull(Long chatId);
    ChatParticipantDto findByChatIdAndUserId(Long chatId, Long userId);
    List<ChatParticipantDto> getChatParticipants(Long chatId);
    List<ChatParticipantDto> getUserParticipations(Long userId);
    List<ChatParticipantDto> getActiveChatParticipants(Long chatId);
    ChatParticipantDto getParticipantById(Long participantId);
    List<ChatParticipantDto> getParticipantsByIds(Set<Long> participantIds);
    ChatParticipantDto getParticipantByChatAndUser(Long chatId, Long userId);
} 