package ru.senla.javacourse.mutovin.messenger.impl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.senla.javacourse.mutovin.messenger.api.controller.FriendRequestController;
import ru.senla.javacourse.mutovin.messenger.api.dto.FriendRequestDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.SuccessResponse;
import ru.senla.javacourse.mutovin.messenger.impl.service.FriendRequestService;
import ru.senla.javacourse.mutovin.messenger.impl.service.UserService;

import java.util.List;

@Tag(name = "Запросы в друзья", description = "API для управления запросами в друзья")
@RestController
@RequestMapping("/api/friend-request")
@RequiredArgsConstructor
public class FriendRequestControllerImpl implements FriendRequestController {

    private final FriendRequestService friendRequestService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(FriendRequestControllerImpl.class);


    @Operation(summary = "Отправить запрос в друзья", description = "Отправляет запрос на дружбу другому пользователю")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запрос успешно отправлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "Запрос уже существует")
    })
    @PostMapping("/send/{receiverId}")
    public ResponseEntity<?> sendRequest(
            @AuthenticationPrincipal UserDetails userDetails,@PathVariable Long receiverId) {
        Long senderId = userService.findByUsername(userDetails.getUsername()).getId();
        FriendRequestDto request = friendRequestService.sendRequest(senderId,receiverId);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Запрос в друзья успешно отправлен")
                        .data(request).build());

    }

    @Operation(summary = "Принять запрос в друзья",
            description = "Принимает входящий запрос на дружбу")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запрос успешно принят"),
            @ApiResponse(responseCode = "404", description = "Запрос не найден"),
            @ApiResponse(responseCode = "403", description = "Нет прав для принятия запроса")
    })
    @PostMapping("/{requestId}/accept")
    public ResponseEntity<?> acceptRequest(@PathVariable Long requestId) {
        FriendRequestDto request = friendRequestService.acceptRequest(requestId);
        return ResponseEntity.ok(
                SuccessResponse.builder()
                        .success(true)
                        .message("Запрос в друзья успешно принят")
                        .data(request)
                        .build());
    }

    @Operation(summary = "Отклонить запрос в друзья",
            description = "Отклоняет входящий запрос на дружбу")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запрос успешно отклонен"),
            @ApiResponse(responseCode = "404", description = "Запрос не найден"),
            @ApiResponse(responseCode = "403", description = "Нет прав для отклонения запроса")
    })
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<?> rejectRequest(
            @Parameter(description = "ID запроса на дружбу", required = true)
            @PathVariable Long requestId) {
        FriendRequestDto request = friendRequestService.rejectRequest(requestId);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Запрос в друзья успешно отклонен")
                        .data(request).build());

    }

    @Operation(summary = "Получить входящие запросы",
            description = "Возвращает список входящих запросов в друзья")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список запросов успешно получен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        List<FriendRequestDto> requests = friendRequestService.getPendingRequests(userId);
        return ResponseEntity.ok(
                SuccessResponse.builder().success(true).message("Входящие запросы успешно получены")
                        .data(requests).build());
    }

    @Operation(summary = "Получить отправленные запросы",
            description = "Возвращает список отправленных запросов в друзья")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список запросов успешно получен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    @GetMapping("/sent")
    public ResponseEntity<?> getSentRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        List<FriendRequestDto> requests = friendRequestService.getSentRequests(userId);
        return ResponseEntity.ok(SuccessResponse.builder().success(true)
                .message("Отправленные запросы успешно получены").data(requests).build());

    }

    @Operation(summary = "Отменить запрос в друзья",
            description = "Отменяет отправленный запрос на дружбу")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запрос успешно отменен"),
            @ApiResponse(responseCode = "404", description = "Запрос не найден"),
            @ApiResponse(responseCode = "403", description = "Нет прав для отмены запроса")
    })
    @DeleteMapping("/{requestId}")
    public ResponseEntity<?> cancelRequest(
            @Parameter(description = "ID запроса на дружбу", required = true)
            @PathVariable Long requestId) {

        friendRequestService.cancelRequest(requestId);
        return ResponseEntity.ok(SuccessResponse.builder().success(true)
                .message("Запрос в друзья успешно отменен").build());

    }
}