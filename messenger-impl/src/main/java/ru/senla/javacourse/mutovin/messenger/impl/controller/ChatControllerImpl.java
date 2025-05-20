package ru.senla.javacourse.mutovin.messenger.impl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.senla.javacourse.mutovin.messenger.api.controller.ChatController;
import ru.senla.javacourse.mutovin.messenger.api.dto.ChatDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.ChatCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.ErrorResponse;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.SuccessResponse;
import ru.senla.javacourse.mutovin.messenger.impl.service.ChatService;
import ru.senla.javacourse.mutovin.messenger.impl.service.UserService;

import java.util.List;

@Tag(name = "Чаты", description = "API для управления чатами")
@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatControllerImpl implements ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(ChatControllerImpl.class);

    @Operation(summary = "Создать чат", description = "Создает новый групповой чат")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Чат успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PostMapping
    public ResponseEntity<?> createChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChatCreateRequest request) {

        Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();
        if (request==null) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.builder().success(false).status(HttpStatus.BAD_REQUEST.value()).message("Запрос не может быть пустым").build());
        }

        ChatDto chat = chatService.createChat(request.getName(),currentUserId,request.getParticipantIds()
        );

        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Чат успешно создан").data(chat).build());

    }

    @Operation(summary = "Создать приватный чат", description = "Создает приватный чат между двумя пользователями")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Приватный чат успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    @PostMapping("/private/{invitedPersonId}")
    public ResponseEntity<?> createPrivateChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID пользователя, приглашенного в приватный чат")
            @PathVariable Long invitedPersonId) {
        Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();
        ChatDto chat = chatService.createPrivateChat(currentUserId,invitedPersonId);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Приватный чат успешно создан").data(chat).build());

    }

    @Operation(summary = "Получить информацию о приватном чате", description = "Получает информацию о приватном чате по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Информация о чате успешно получена"),
            @ApiResponse(responseCode = "404", description = "Чат не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @GetMapping("/private/{userId2}")
    public ResponseEntity<?> getPrivateChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID второго пользователя") @PathVariable Long userId2) {

        Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();
        ChatDto chat = chatService.getPrivateChat(currentUserId, userId2);
        return ResponseEntity.ok(
                SuccessResponse.builder()
                        .success(true)
                        .message("Информация о чате успешно получена")
                        .data(chat)
                        .build());
    }

    @Operation(summary = "Удалить чат", description = "Удаляет чат по его ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Чат успешно удален"),
            @ApiResponse(responseCode = "404", description = "Чат не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @DeleteMapping("/{chatId}")
    public ResponseEntity<?> deleteChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID чата") @PathVariable Long chatId) {

        Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();
        chatService.deleteChat(chatId,currentUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Добавить участника", description = "Добавляет пользователя в чат")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Участник успешно добавлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Чат не найден")
    })
    @PostMapping("/{chatId}/participants")
    public ResponseEntity<?> addParticipant(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID чата") @PathVariable Long chatId) {
        Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();
        ChatDto chat = chatService.addParticipant(chatId,currentUserId);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Участник успешно добавлен").data(chat).build());

    }

    @Operation(summary = "Удалить участника", description = "Удаляет пользователя из чата")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Участник успешно удален"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Чат или пользователь не найден")
    })
    @DeleteMapping("/{chatId}/participants/{userId}")
    public ResponseEntity<?> removeParticipant(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID чата") @PathVariable Long chatId) {

        Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();
        ChatDto chat = chatService.removeParticipant(chatId,currentUserId);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Участник успешно удален").data(chat).build());

    }

    @Operation(summary = "Назначить администратора", description = "Назначает пользователя администратором чата")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Администратор успешно назначен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Чат или пользователь не найден")
    })
    @PostMapping("/{chatId}/admins/{userId}")
    public ResponseEntity<?> makeAdmin(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID чата") @PathVariable Long chatId) {

        Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();
        ChatDto chat = chatService.makeAdmin(chatId,currentUserId);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Администратор успешно назначен").data(chat).build());

    }

    @Operation(summary = "Получить чаты пользователя", description = "Возвращает список всех чатов пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список чатов успешно получен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserChats(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID пользователя") @PathVariable Long userId) {

        userService.findByUsername(userDetails.getUsername());
        List<ChatDto> chats = chatService.getUserChats(userId);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Список чатов успешно получен").data(chats).build());

    }

    @Operation(summary = "Получить чат по ID", description = "Возвращает информацию о чате по его ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Чат успешно получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Чат не найден")
    })
    @GetMapping("/{chatId}")
    public ResponseEntity<?> getChatById(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID чата") @PathVariable Long chatId) {
        Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();
        ChatDto chat = chatService.getChatById(chatId,currentUserId);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Чат успешно получен").data(chat).build());

    }
}