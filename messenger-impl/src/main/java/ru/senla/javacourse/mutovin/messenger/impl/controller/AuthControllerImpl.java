package ru.senla.javacourse.mutovin.messenger.impl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.senla.javacourse.mutovin.messenger.api.controller.AuthController;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.SignInRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.SignUpRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.ErrorResponse;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.SuccessResponse;
//import ru.senla.javacourse.mutovin.messenger.impl.kafka.KafkaProducer;
import ru.senla.javacourse.mutovin.messenger.impl.service.AuthenticationService;


@RestController
@RequestMapping("/api/auth")
//@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class AuthControllerImpl implements AuthController {
    private final AuthenticationService authenticationService;
//    private final KafkaProducer kafkaProducer;
    private static final Logger logger = LoggerFactory.getLogger(AuthControllerImpl.class);

    @Autowired
    public AuthControllerImpl(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
//        this.kafkaProducer = kafkaProducer;
    }
    /**
     * @param request
     * @return
     */
    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.builder()
                    .success(true).message("Пользователь успешно создан")
                    .data(authenticationService.signUp(request)).build());
        }
        catch (RuntimeException e){
            logger.error("Ошибка при создании пользователя: {}",e.getMessage());
            return ResponseEntity.badRequest().body(ErrorResponse.builder()
                    .success(false).status(HttpStatus.BAD_REQUEST.value())
                    .message("Ошибка при создании пользователя").details(e.getMessage()).build());
        }

    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInRequest request) {
        try {
//            kafkaProducer.send("notification",request.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.builder()
                    .success(true).message("Аунтефикация прошла успешно")
                    .data(authenticationService.signIn(request)).build());
        }
        catch (RuntimeException e){
            logger.error("Ошибка при аунтефикации: {}",e.getMessage());
            return ResponseEntity.badRequest().body(ErrorResponse.builder()
                    .success(false).status(HttpStatus.BAD_REQUEST.value())
                    .message("Ошибка при аунтефикации:").details(e.getMessage()).build());

        }
    }
    @Operation(summary = "Проверка валидности токена")
    @GetMapping("/token")
    public ResponseEntity<?> verifyToken(HttpServletRequest request) {

        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.builder()
                    .success(true).message("Токен валиден")
                    .data(authenticationService.checkVerifyToken(request)).build());
        }
        catch (RuntimeException e){
            logger.error("Ошибка: {}",e.getMessage());
            return ResponseEntity.badRequest().body(ErrorResponse.builder()
                    .success(false).status(HttpStatus.BAD_REQUEST.value())
                    .message("Ошибка:").details(e.getMessage()).build());
        }

    }
}


