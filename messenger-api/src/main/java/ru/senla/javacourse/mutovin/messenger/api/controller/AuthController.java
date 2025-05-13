package ru.senla.javacourse.mutovin.messenger.api.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.SignInRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.SignUpRequest;

public interface AuthController {
    ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequest request);
    ResponseEntity<?> signIn(@RequestBody @Valid SignInRequest request);
    ResponseEntity<?> verifyToken(HttpServletRequest request);
}
