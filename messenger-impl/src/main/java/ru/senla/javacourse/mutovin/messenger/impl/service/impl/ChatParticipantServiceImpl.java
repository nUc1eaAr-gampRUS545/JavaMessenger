package ru.senla.javacourse.mutovin.messenger.impl.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.javacourse.mutovin.messenger.api.dto.ChatParticipantDto;
import ru.senla.javacourse.mutovin.messenger.impl.exception.ChatParticipantException;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.ChatParticipantMapper;
import ru.senla.javacourse.mutovin.messenger.impl.repository.ChatParticipantRepository;
import ru.senla.javacourse.mutovin.messenger.impl.service.ChatParticipantService;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatParticipantServiceImpl implements ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatParticipantMapper chatParticipantMapper;

    @Override
    @Transactional
    public List<ChatParticipantDto> findByChatId(Long chatId) {
        return chatParticipantRepository.findByChatId(chatId)
                .orElseThrow(() -> new ChatParticipantException("Не удалось найти участников чата: " + chatId))
                .stream()
                .map(chatParticipantMapper::map)
                .toList();
    }

    @Override
    @Transactional
    public List<ChatParticipantDto> findByUserId(Long userId) {
        return chatParticipantRepository.findByUserId(userId)
                .orElseThrow(() -> new ChatParticipantException("Не удалось найти чаты пользователя: " + userId))
                .stream()
                .map(chatParticipantMapper::map)
                .toList();
    }

    @Override
    @Transactional
    public List<ChatParticipantDto> findByChatIdAndLeftAtIsNull(Long chatId) {
        return chatParticipantRepository.findActiveParticipantsByChatId(chatId)
                .orElseThrow(() -> new ChatParticipantException("Не удалось найти активных участников чата: " + chatId))
                .stream()
                .map(chatParticipantMapper::map)
                .toList();
    }

    @Override
    @Transactional
    public ChatParticipantDto findByChatIdAndUserId(Long chatId, Long userId) {
        return chatParticipantRepository.findByChatIdAndUserId(chatId, userId)
                .map(chatParticipantMapper::map)
                .orElseThrow(() -> new ChatParticipantException.ChatParticipantNotFoundException(userId));
    }

    @Override
    @Transactional
    public List<ChatParticipantDto> getChatParticipants(Long chatId) {
        return findByChatId(chatId);
    }

    @Override
    @Transactional
    public List<ChatParticipantDto> getUserParticipations(Long userId) {
        return findByUserId(userId);
    }

    @Override
    @Transactional
    public List<ChatParticipantDto> getActiveChatParticipants(Long chatId) {
        return findByChatIdAndLeftAtIsNull(chatId);
    }

    @Override
    @Transactional
    public ChatParticipantDto getParticipantById(Long participantId) {
        return chatParticipantRepository.findById(participantId)
                .map(chatParticipantMapper::map)
                .orElseThrow(() -> new ChatParticipantException.ChatParticipantNotFoundException(participantId));
    }

    @Override
    @Transactional
    public List<ChatParticipantDto> getParticipantsByIds(Set<Long> participantIds) {
        return chatParticipantRepository.findAllByParticipantIds(participantIds)
                .orElseThrow(() -> new ChatParticipantException("Не удалось найти участников по указанным id"))
                .stream()
                .map(chatParticipantMapper::map)
                .toList();
    }

    @Override
    @Transactional
    public ChatParticipantDto getParticipantByChatAndUser(Long chatId, Long userId) {
        return findByChatIdAndUserId(chatId, userId);
    }
} 