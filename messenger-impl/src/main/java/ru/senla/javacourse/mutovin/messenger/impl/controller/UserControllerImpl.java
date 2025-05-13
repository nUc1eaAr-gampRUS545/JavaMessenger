package ru.senla.javacourse.mutovin.messenger.impl.controller;

import ru.senla.javacourse.mutovin.messenger.api.controller.UserController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.ErrorResponse;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.SuccessResponse;
import ru.senla.javacourse.mutovin.messenger.impl.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "Пользователи")
public class UserControllerImpl implements UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserControllerImpl.class);
    private final UserService userService;

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            UserDto user = userService.findByIdWithFriends(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Error getting user by id: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder()
                            .success(false)
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("Пользователь не найден")
                            .details(e.getMessage())
                            .build());
        }
    }

    @Override
    @GetMapping("/all")
    @Operation(summary = "Получить всех пользователей")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserDto> users = userService.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .success(false)
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Ошибка при получении списка пользователей")
                            .details(e.getMessage())
                            .build());
        }
    }


    @Override
    @DeleteMapping("/{userId}/friend/{friendId}")
    @Operation(summary = "Удалить друга")
    public ResponseEntity<?> removeFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        try {
            userService.removeFriend(userId, friendId);
            return ResponseEntity.ok(SuccessResponse.builder()
                    .success(true)
                    .message("Друг успешно удален")
                    .build());
        } catch (Exception e) {
            logger.error("Error removing friend: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .success(false)
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Ошибка при удалении друга")
                            .details(e.getMessage())
                            .build());
        }
    }

    @Override
    @PostMapping("/admin")
    @Operation(summary = "Получить права администратора")
    public ResponseEntity<?> getAdmin() {
        try {
            userService.getAdmin();
            return ResponseEntity.ok(SuccessResponse.builder()
                    .success(true)
                    .message("Права администратора получены")
                    .build());
        } catch (Exception e) {
            logger.error("Error getting admin rights: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .success(false)
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Ошибка при получении прав администратора")
                            .details(e.getMessage())
                            .build());
        }
    }
}

