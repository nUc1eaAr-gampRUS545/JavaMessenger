package ru.senla.javacourse.mutovin.messenger.impl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.senla.javacourse.mutovin.messenger.api.dto.ChatParticipantDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.SuccessResponse;
import ru.senla.javacourse.mutovin.messenger.db.entity.ChatParticipant;
import ru.senla.javacourse.mutovin.messenger.impl.service.ChatParticipantService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "Участники чата", description = "API для управления участниками чата")
@RestController
@RequestMapping("/api/chat-participants")
@RequiredArgsConstructor
public class ChatParticipantControllerImpl {

    private final ChatParticipantService chatParticipantService;

    @Operation(summary = "Получить участников чата", description = "Возвращает список всех участников чата")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список участников успешно получен"),
        @ApiResponse(responseCode = "404", description = "Чат не найден")
    })
    @GetMapping("/chat/{chatId}")
    public ResponseEntity<?> getChatParticipants(
            @Parameter(description = "ID чата") @PathVariable Long chatId) {
        List<ChatParticipantDto> participants = chatParticipantService.getChatParticipants(chatId);
        return ResponseEntity.ok(
                SuccessResponse
                        .builder()
                        .success(true)
                        .message("Список участников успешно получен")
                        .data(participants)
                        .build());
    }

    @Operation(summary = "Получить чаты пользователя", description = "Возвращает список всех чатов, в которых участвует пользователь")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список чатов успешно получен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserParticipations(
            @Parameter(description = "ID пользователя") @PathVariable Long userId) {
        List<ChatParticipantDto> participants = chatParticipantService.getUserParticipations(userId);

        return ResponseEntity.ok(
                SuccessResponse
                        .builder()
                        .success(true)
                        .message("Список чатов успешно получен")
                        .data(participants)
                        .build());
    }

    @Operation(summary = "Получить активных участников", description = "Возвращает список активных участников чата")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список активных участников успешно получен"),
        @ApiResponse(responseCode = "404", description = "Чат не найден")
    })
    @GetMapping("/chat/{chatId}/active")
    public ResponseEntity<?> getActiveChatParticipants(
            @Parameter(description = "ID чата") @PathVariable Long chatId) {
        List<ChatParticipantDto> participants = chatParticipantService.getActiveChatParticipants(chatId);

        return ResponseEntity.ok(
                SuccessResponse
                        .builder()
                        .success(true)
                        .message("Список активных участников успешно получен")
                        .data(participants)
                        .build());
    }

    @Operation(summary = "Получить участника по ID", description = "Возвращает информацию об участнике чата по его ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Участник успешно найден"),
        @ApiResponse(responseCode = "404", description = "Участник не найден")
    })
    @GetMapping("/{participantId}")
    public ResponseEntity<?> getParticipantById(
            @Parameter(description = "ID участника") @PathVariable Long participantId) {
        ChatParticipantDto participantDto = chatParticipantService.getParticipantById(participantId);
        return ResponseEntity.ok(
                SuccessResponse
                        .builder()
                        .success(true)
                        .message("Список активных участников успешно получен")
                        .data(participantDto)
                        .build());
    }

    @Operation(summary = "Получить участников по ID", description = "Возвращает список участников чата по их ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список участников успешно получен")
    })
    @PostMapping("/batch")
    public ResponseEntity<?> getParticipantsByIds(
            @Parameter(description = "Список ID участников") @RequestBody Set<Long> participantIds) {
        List<ChatParticipantDto> participants = chatParticipantService.getParticipantsByIds(participantIds);

        return ResponseEntity.ok(
                SuccessResponse
                        .builder()
                        .success(true)
                        .message("Список участников успешно получен")
                        .data(participants)
                        .build());
    }

    @Operation(summary = "Получить участника по чату и пользователю", description = "Возвращает информацию об участнике чата по ID чата и ID пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Участник успешно найден"),
        @ApiResponse(responseCode = "404", description = "Участник не найден")
    })
    @GetMapping("/chat/{chatId}/user/{userId}")
    public ResponseEntity<?> getParticipantByChatAndUser(
            @Parameter(description = "ID чата") @PathVariable Long chatId,
            @Parameter(description = "ID пользователя") @PathVariable Long userId) {
        ChatParticipantDto participantDto = chatParticipantService.getParticipantByChatAndUser(chatId, userId);

        return ResponseEntity.ok(
                SuccessResponse
                        .builder()
                        .success(true)
                        .message("Список участников успешно получен")
                        .data(participantDto)
                        .build());
    }
} 