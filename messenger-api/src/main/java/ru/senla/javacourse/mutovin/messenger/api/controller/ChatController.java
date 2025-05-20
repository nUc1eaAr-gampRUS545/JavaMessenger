package ru.senla.javacourse.mutovin.messenger.api.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.ChatCreateRequest;

public interface ChatController {
    ResponseEntity<?> createChat(@AuthenticationPrincipal UserDetails userDetails,@RequestBody ChatCreateRequest request);

    ResponseEntity<?> createPrivateChat(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long invitedPersonId);

    ResponseEntity<?> getPrivateChat(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long userId2);

    ResponseEntity<?> deleteChat(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long chatId);

    ResponseEntity<?> addParticipant(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long chatId);

    ResponseEntity<?> removeParticipant(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long chatId);

    ResponseEntity<?> makeAdmin(@AuthenticationPrincipal UserDetails userDetails,@Parameter(description = "ID чата") @PathVariable Long chatId);

    ResponseEntity<?> getUserChats(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long userId);

    ResponseEntity<?> getChatById(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long chatId);
}
