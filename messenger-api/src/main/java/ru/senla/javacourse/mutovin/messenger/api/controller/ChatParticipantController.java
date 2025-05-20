package ru.senla.javacourse.mutovin.messenger.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

public interface ChatParticipantController {
    ResponseEntity<?> getChatParticipants(@PathVariable Long chatId);

    ResponseEntity<?> getUserParticipations(@PathVariable Long userId);

    ResponseEntity<?> getActiveChatParticipants(@PathVariable Long chatId);

    ResponseEntity<?> getParticipantById(@PathVariable Long participantId);

    ResponseEntity<?> getParticipantByChatAndUser(@PathVariable Long chatId,@PathVariable Long userId);
}
