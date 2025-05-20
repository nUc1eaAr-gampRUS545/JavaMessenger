package ru.senla.javacourse.mutovin.messenger.impl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.senla.javacourse.mutovin.messenger.api.controller.MessageController;
import ru.senla.javacourse.mutovin.messenger.api.dto.MessageDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.MessageCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.MessageUpdateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.SuccessResponse;
import ru.senla.javacourse.mutovin.messenger.db.entity.Message;
import ru.senla.javacourse.mutovin.messenger.db.entity.MessageStatus;
import ru.senla.javacourse.mutovin.messenger.impl.service.MessageService;
import ru.senla.javacourse.mutovin.messenger.impl.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "Сообщения", description = "API для управления сообщениями")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageControllerImpl implements MessageController {

    private final MessageService messageService;
    private final UserService userService;
    @Operation(summary = "Создать сообщение", description = "Создает новое сообщение в чате")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Сообщение успешно создано"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
        @ApiResponse(responseCode = "404", description = "Чат или отправитель не найдены")
    })
    @PostMapping
    public ResponseEntity<?> createMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MessageCreateRequest request) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        MessageDto message = messageService.createMessage(request.getChatId(), userId, request.getContent());
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Сообщение успешно создано")
                        .data(message).build());
    }

    @Operation(summary = "Обновить сообщение", description = "Обновляет содержимое сообщения")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Сообщение успешно обновлено"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
        @ApiResponse(responseCode = "404", description = "Сообщение не найдено")
    })
    @PutMapping("/{messageId}")
    public ResponseEntity<?> updateMessage(
            @Parameter(description = "ID сообщения") @PathVariable Long messageId,
            @RequestBody MessageUpdateRequest request) {
        MessageDto message = messageService.updateMessage(messageId, request.getContent());

        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Сообщение успешно обновлено")
                        .data(message).build());
    }

    @Operation(summary = "Удалить сообщение", description = "Удаляет сообщение")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Сообщение успешно удалено"),
        @ApiResponse(responseCode = "404", description = "Сообщение не найдено")
    })
    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(
            @Parameter(description = "ID сообщения") @PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Сообщение успешно удалено")
                        .data(null).build());
    }

    @PutMapping("/{messageId}/read")
    public ResponseEntity<?> markMessageAsRead(@PathVariable Long messageId) {
        MessageDto message = messageService.markMessageAsRead(messageId);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Сообщение успешно прочитано")
                        .data(message).build());
    }

    @PutMapping("/{messageId}/delivered")
    public ResponseEntity<?> markMessageAsDelivered(@PathVariable Long messageId) {
        MessageDto message = messageService.markMessageAsDelivered(messageId);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Сообщение успешно отправлено")
                        .data(message).build());
    }

    @Operation(summary = "Получить сообщения чата", description = "Возвращает список сообщений чата")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список сообщений успешно получен"),
        @ApiResponse(responseCode = "404", description = "Чат не найден")
    })
    @GetMapping("/chat/{chatId}")
    public ResponseEntity<?> getChatMessages(
            @Parameter(description = "ID чата") @PathVariable Long chatId) {
        List<MessageDto> messages = messageService.getChatMessages(chatId);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Список сообщений успешно получен")
                        .data(messages).build());
    }

    @Operation(summary = "Получить сообщения пользователя", description = "Возвращает список сообщений, отправленных пользователем")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список сообщений успешно получен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserMessages(
            @Parameter(description = "ID пользователя") @PathVariable Long userId) {
        List<MessageDto> messages = messageService.getUserMessages(userId);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Список сообщений успешно получен")
                        .data(messages).build());
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<?> getUnreadMessages(@PathVariable Long userId) {
        List<MessageDto> messages = messageService.getUnreadMessages(userId);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Список непрочитанных сообщений успешно получен")
                        .data(messages).build());
    }

    @Operation(summary = "Получить сообщение по ID", description = "Возвращает информацию о сообщении по его ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Сообщение успешно найдено"),
        @ApiResponse(responseCode = "404", description = "Сообщение не найдено")
    })
    @GetMapping("/{messageId}")
    public ResponseEntity<?> getMessageById(
            @Parameter(description = "ID сообщения") @PathVariable Long messageId) {
        MessageDto message =  messageService.getMessageById(messageId);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Сообщение успешно найдено")
                        .data(message).build());
    }

    @GetMapping("/chat/{chatId}/status/{status}")
    public ResponseEntity<?> getMessagesByStatus(
            @PathVariable Long chatId,
            @PathVariable MessageStatus status) {
        List<MessageDto> messages = messageService.getMessagesByStatus(chatId, status);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Сообщения получены")
                        .data(messages).build());
    }

    @Operation(summary = "Получить сообщения по ID", description = "Возвращает список сообщений по их ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список сообщений успешно получен")
    })
    @PostMapping("/batch")
    public ResponseEntity<?> getMessagesByIds(
            @Parameter(description = "Список ID сообщений") @RequestBody Set<Long> messageIds) {
        List<MessageDto> messages = messageService.getMessagesByIds(messageIds);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Сообщения получены")
                        .data(messages).build());
    }
} 