package ru.senla.javacourse.mutovin.messenger.impl.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import ru.senla.javacourse.mutovin.messenger.api.controller.UserController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.UserUpdateRequest;
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
        UserDto user = userService.findByIdWithFriendsAndPosts(id);
        return ResponseEntity.ok(user);
    }

    @Override
    @GetMapping("/filter")
    @Operation(summary = "Получить пользователя по имени, фамилии, полу и возрасту")
    public ResponseEntity<?> filterUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String gender
    )
    {return ResponseEntity.ok(userService.filterUsers(firstName,lastName,age,gender));}

    @Override
    @GetMapping
    @Operation(summary = "Получить пользователя по ID")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        UserDto user = userService.findByIdWithFriendsAndPosts(userId);

        return ResponseEntity.ok(user);
    }

    @Override
    @GetMapping("/all")
    @Operation(summary = "Получить всех пользователей")
    public ResponseEntity<?> getAllUsers() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(users);

    }


    @Override
    @DeleteMapping("/friend/{friendId}")
    @Operation(summary = "Удалить друга")
    public ResponseEntity<?> removeFriend(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long friendId) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        userService.removeFriend(userId,friendId);
        return ResponseEntity.ok(SuccessResponse.builder().success(true).message("Друг успешно удален")
                .build());
    }

    @Override
    @PostMapping("/admin")
    @Operation(summary = "Получить права администратора")
    public ResponseEntity<?> getAdmin() {
        userService.getAdmin();
        return ResponseEntity.ok(SuccessResponse.builder().success(true)
                .message("Права администратора получены").build());

    }

    @Override
    @PostMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserUpdateRequest request
    ) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        UserDto updated = userService.updateUserProfile(userId,request);
        return ResponseEntity.ok(SuccessResponse.builder().success(true)
                .message("Профиль успешно обновлен").data(updated).build());
    }
}

