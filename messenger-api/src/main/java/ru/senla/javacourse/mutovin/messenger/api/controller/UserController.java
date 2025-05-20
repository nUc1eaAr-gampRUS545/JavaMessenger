package ru.senla.javacourse.mutovin.messenger.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.UserUpdateRequest;

public interface UserController {

    ResponseEntity<?> getUserById(@PathVariable Long id);

    ResponseEntity<?> filterUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String gender);

    ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails);

    ResponseEntity<?> getAllUsers();

    ResponseEntity<?> removeFriend(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long friendId);

    ResponseEntity<?> getAdmin();

    ResponseEntity<?> updateMyProfile(@AuthenticationPrincipal UserDetails userDetails,@RequestBody UserUpdateRequest request);
}
