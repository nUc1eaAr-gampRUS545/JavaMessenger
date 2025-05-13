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
import ru.senla.javacourse.mutovin.messenger.api.dto.ChatDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.ChatCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PrivateChatCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.ErrorResponse;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.SuccessResponse;
import ru.senla.javacourse.mutovin.messenger.impl.exception.ChatException;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.ChatMapper;
import ru.senla.javacourse.mutovin.messenger.impl.service.ChatService;
import ru.senla.javacourse.mutovin.messenger.impl.service.UserService;

import java.util.List;

@Tag(name = "Чаты", description = "API для управления чатами")
@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatControllerImpl {

    private final ChatService chatService;
    private final UserService userService;
    private final ChatMapper chatMapper;
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
        try {
            Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();

            if (request == null) {
                return ResponseEntity.badRequest().body(
                        ErrorResponse.builder().success(false).status(HttpStatus.BAD_REQUEST.value()).message("Запрос не может быть пустым").build());
            }

            ChatDto chat = chatService.createChat(request.getName(),currentUserId,request.getParticipantIds()
            );

            return ResponseEntity.ok(
                    SuccessResponse.builder().success(true).message("Чат успешно создан").data(chat).build());

        } catch (ChatException e) {
            logger.error("Ошибка при создании чата: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ErrorResponse.builder().success(false).status(HttpStatus.BAD_REQUEST.value()).message(e.getMessage()).build());
        } catch (Exception e) {
            logger.error("Внутренняя ошибка при создании чата: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ErrorResponse.builder().success(false).status(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("Внутренняя ошибка сервера").build());
        }
    }

    @Operation(summary = "Создать приватный чат", description = "Создает приватный чат между двумя пользователями")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Приватный чат успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    @PostMapping("/private")
    public ResponseEntity<?> createPrivateChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PrivateChatCreateRequest request) {
        try {
            Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();

            if (request == null || request.getSecondUserId() == null) {
                return ResponseEntity.badRequest().body(
                        ErrorResponse.builder().success(false).status(HttpStatus.BAD_REQUEST.value()).message("ID второго пользователя не может быть пустым").build());
            }

            ChatDto chat = chatService.createPrivateChat(currentUserId, request.getSecondUserId());

            return ResponseEntity.ok(
                    SuccessResponse.builder().success(true).message("Приватный чат успешно создан").data(chat).build());

        } catch (ChatException.PrivateChatAlreadyExistsException e) {
            logger.error("Приватный чат уже существует: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ErrorResponse.builder().success(false).status(HttpStatus.CONFLICT.value()).message(e.getMessage()).build());
        } catch (ChatException e) {
            logger.error("Ошибка при создании приватного чата: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ErrorResponse.builder().success(false).status(HttpStatus.BAD_REQUEST.value()).message(e.getMessage()).build());
        }
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
        try {
            Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();
            chatService.deleteChat(chatId, currentUserId);
            return ResponseEntity.noContent().build();
        } catch (ChatException.ChatNotFoundException e) {
            logger.error("Чат не найден: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ErrorResponse.builder().success(false).status(HttpStatus.NOT_FOUND.value()).message(e.getMessage()).build());
        } catch (ChatException.UserAccessDeniedException e) {
            logger.error("Доступ запрещен: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ErrorResponse.builder().success(false).status(HttpStatus.FORBIDDEN.value()).message(e.getMessage()).build());
        }
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
        try {
            Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();

            ChatDto chat = chatService.addParticipant(chatId, currentUserId);

            return ResponseEntity.ok(
                    SuccessResponse.builder().success(true).message("Участник успешно добавлен").data(chat).build());

        } catch (ChatException.ChatNotFoundException e) {
            logger.error("Чат не найден: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ErrorResponse.builder().success(false).status(HttpStatus.NOT_FOUND.value()).message(e.getMessage()).build());
        } catch (ChatException.UserAccessDeniedException e) {
            logger.error("Доступ запрещен: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ErrorResponse.builder().success(false).status(HttpStatus.FORBIDDEN.value()).message(e.getMessage()).build());
        }
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
        try {
            Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();

            ChatDto chat = chatService.removeParticipant(chatId, currentUserId);

            return ResponseEntity.ok(
                    SuccessResponse.builder().success(true).message("Участник успешно удален").data(chat).build());

        } catch (ChatException.ChatNotFoundException e) {
            logger.error("Чат не найден: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ErrorResponse.builder().success(false).status(HttpStatus.NOT_FOUND.value()).message(e.getMessage()).build());
        } catch (ChatException.UserAccessDeniedException e) {
            logger.error("Доступ запрещен: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ErrorResponse.builder().success(false).status(HttpStatus.FORBIDDEN.value()).message(e.getMessage()).build());
        }
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
        try {
            Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();

            ChatDto chat = chatService.makeAdmin(chatId,  currentUserId);

            return ResponseEntity.ok(
                    SuccessResponse.builder().success(true).message("Администратор успешно назначен").data(chat).build());

        } catch (ChatException.ChatNotFoundException e) {
            logger.error("Чат не найден: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ErrorResponse.builder().success(false).status(HttpStatus.NOT_FOUND.value()).message(e.getMessage()).build());
        } catch (ChatException.UserAccessDeniedException e) {
            logger.error("Доступ запрещен: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ErrorResponse.builder().success(false).status(HttpStatus.FORBIDDEN.value()).message(e.getMessage()).build());
        }
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
        try {
            userService.findByUsername(userDetails.getUsername()); // Проверка авторизации

            List<ChatDto> chats = chatService.getUserChats(userId);

            return ResponseEntity.ok(
                    SuccessResponse.builder().success(true).message("Список чатов успешно получен").data(chats).build());

        } catch (Exception e) {
            logger.error("Ошибка при получении чатов пользователя: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ErrorResponse.builder().success(false).status(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("Ошибка при получении чатов пользователя").build());
        }
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
        try {
            Long currentUserId = userService.findByUsername(userDetails.getUsername()).getId();

            ChatDto chat = chatService.getChatById(chatId, currentUserId);

            return ResponseEntity.ok(
                    SuccessResponse.builder().success(true).message("Чат успешно получен").data(chat).build());

        } catch (ChatException.ChatNotFoundException e) {
            logger.error("Чат не найден: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ErrorResponse.builder().success(false).status(HttpStatus.NOT_FOUND.value()).message(e.getMessage()).build());
        } catch (ChatException.UserAccessDeniedException e) {
            logger.error("Доступ запрещен: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ErrorResponse.builder().success(false).status(HttpStatus.FORBIDDEN.value()).message(e.getMessage()).build());
        }
    }
}