package ru.senla.javacourse.mutovin.messenger.impl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.senla.javacourse.mutovin.messenger.api.controller.AuthController;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.SignInRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.SignUpRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.SuccessResponse;
import ru.senla.javacourse.mutovin.messenger.impl.service.AuthenticationService;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class AuthControllerImpl implements AuthController {
    private final AuthenticationService authenticationService;
    private static final Logger logger = LoggerFactory.getLogger(AuthControllerImpl.class);

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.builder()
                .success(true).message("Пользователь успешно создан")
                .data(authenticationService.signUp(request)).build());
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.builder()
                .success(true).message("Аунтефикация прошла успешно")
                .data(authenticationService.signIn(request)).build());

    }

}


