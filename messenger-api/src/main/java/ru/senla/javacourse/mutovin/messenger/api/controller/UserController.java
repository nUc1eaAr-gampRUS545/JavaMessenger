package ru.senla.javacourse.mutovin.messenger.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface UserController {
    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID")
    ResponseEntity<?> getUserById(@PathVariable Long id);

    @GetMapping("/all")
    @Operation(summary = "Получить всех пользователей")
    ResponseEntity<?> getAllUsers();

    @DeleteMapping("/{userId}/friend/{friendId}")
    @Operation(summary = "Удалить друга")
    ResponseEntity<?> removeFriend(@PathVariable Long userId, @PathVariable Long friendId);

    @PostMapping("/admin")
    @Operation(summary = "Получить права администратора")
    ResponseEntity<?> getAdmin();
}
