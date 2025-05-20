package ru.senla.javacourse.mutovin.messenger.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.senla.javacourse.mutovin.messenger.api.dto.MessageStatus;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.MessageCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.MessageUpdateRequest;

import java.util.Set;

public interface MessageController {
    ResponseEntity<?> createMessage(@AuthenticationPrincipal UserDetails userDetails,@RequestBody MessageCreateRequest request);

    ResponseEntity<?> updateMessage(@PathVariable Long messageId,@RequestBody MessageUpdateRequest request);

    ResponseEntity<?> deleteMessage(@PathVariable Long messageId);

    ResponseEntity<?> markMessageAsRead(@PathVariable Long messageId);

    ResponseEntity<?> markMessageAsDelivered(@PathVariable Long messageId);

    ResponseEntity<?> getChatMessages(@PathVariable Long chatId);

    ResponseEntity<?> getUserMessages(@PathVariable Long userId);

    ResponseEntity<?> getUnreadMessages(@PathVariable Long userId);

    ResponseEntity<?> getMessageById(@PathVariable Long messageId);


    ResponseEntity<?> getMessagesByIds(@RequestBody Set<Long> messageIds);
}
