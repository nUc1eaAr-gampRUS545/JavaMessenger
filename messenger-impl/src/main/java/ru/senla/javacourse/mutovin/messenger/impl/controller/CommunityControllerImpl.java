package ru.senla.javacourse.mutovin.messenger.impl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.senla.javacourse.mutovin.messenger.api.controller.CommunityController;
import ru.senla.javacourse.mutovin.messenger.api.dto.CommunityDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.CommunityCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.SuccessResponse;
import ru.senla.javacourse.mutovin.messenger.impl.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.senla.javacourse.mutovin.messenger.impl.service.UserService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Tag(name = "Сообщества", description = "API для управления сообществами")
@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class CommunityControllerImpl implements CommunityController {

    private final CommunityService communityService;
    private static final Logger logger = LoggerFactory.getLogger(CommunityControllerImpl.class);
    private final UserService userService;

    @Override
    @PostMapping
    @Operation(summary = "Создать сообщество", description = "Создает новое сообщество")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Успешное создание"),
        @ApiResponse(responseCode = "400", description = "Неверные данные"),
        @ApiResponse(responseCode = "403", description = "Нет прав на создание")
    })

    public ResponseEntity<?> createCommunity(@RequestBody CommunityCreateRequest request) {
        CommunityDto community = communityService.createCommunity(request);
        return ResponseEntity.ok(SuccessResponse.builder()
                .success(true)
                .message("Сообщество успешно создано")
                .data(community)
                .build());
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "Получить сообщество", description = "Возвращает информацию о сообществе")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Сообщество успешно получено"),
        @ApiResponse(responseCode = "404", description = "Сообщество не найдено")
    })
    public ResponseEntity<?> getCommunity(@PathVariable Long id) {
        CommunityDto community = communityService.getCommunity(id);
        return ResponseEntity.ok(SuccessResponse.builder()
                .success(true)
                .message("Сообщество успешно получено")
                .data(community)
                .build());
    }

    @Override
    @PostMapping("/{communityId}/create_post")
    @Operation(summary = "Создать пост внутри сообщества", description = "Создает пост внутри сообщества")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно создан"),
            @ApiResponse(responseCode = "404", description = "Сообщество не найдено"),
            @ApiResponse(responseCode = "403", description = "Нет прав на создание поста")
    })

    public ResponseEntity<?> createPostInCommunity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long communityId,
            @RequestBody PostCreateRequest request) throws AccessDeniedException {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        CommunityDto community = communityService.createPostInCommunity(userId,communityId,request);
        return ResponseEntity.ok(SuccessResponse.builder()
                .success(true)
                .message("Сообщество успешно обновлено")
                .data(community)
                .build());
    }

    @Override
    @PutMapping("/{id}")
    @Operation(summary = "Обновить сообщество", description = "Обновляет информацию о сообществе")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешное обновление"),
            @ApiResponse(responseCode = "404", description = "Сообщество не найдено"),
            @ApiResponse(responseCode = "403", description = "Нет прав на обновление")
    })

    public ResponseEntity<?> updateCommunity(@PathVariable Long id, @RequestBody CommunityCreateRequest request) {
        CommunityDto community = communityService.updateCommunity(id,request);
        return ResponseEntity.ok(SuccessResponse.builder()
                .success(true)
                .message("Сообщество успешно обновлено")
                .data(community)
                .build());
    }

    @Override
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить сообщество", description = "Удаляет сообщество")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Успешное удаление"),
        @ApiResponse(responseCode = "404", description = "Сообщество не найдено"),
        @ApiResponse(responseCode = "403", description = "Нет прав на удаление")
    })
    public ResponseEntity<?> deleteCommunity(Long id) {
        communityService.deleteCommunity(id);
        return ResponseEntity.ok(SuccessResponse.builder()
                .success(true)
                .message("Сообщество успешно удалено")
                .build());
    }

    @Override
    @PostMapping("/{communityId}/join")
    @Operation(summary = "Присоединиться к сообществу", description = "Присоединяет пользователя к сообществу")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Успешное присоединение"),
        @ApiResponse(responseCode = "404", description = "Сообщество не найдено"),
        @ApiResponse(responseCode = "403", description = "Нет прав на присоединение")
    })

    public ResponseEntity<?> joinCommunity(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long communityId) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        CommunityDto community = communityService.joinCommunity(userId, communityId);
        return ResponseEntity.ok(SuccessResponse.builder()
                .success(true)
                .message("Успешно присоединились к сообществу")
                .data(community)
                .build());
    }

    @Override
    @PostMapping("/{communityId}/leave")
    @Operation(summary = "Покинуть сообщество", description = "Покидает сообщество")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Успешное покидание"),
        @ApiResponse(responseCode = "404", description = "Сообщество не найдено"),
        @ApiResponse(responseCode = "403", description = "Нет прав на покидание")
    })

    public ResponseEntity<?> leaveCommunity(@AuthenticationPrincipal UserDetails userDetails, Long communityId) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        communityService.leaveCommunity(userId, communityId);
        return ResponseEntity.ok(SuccessResponse.builder()
                .success(true)
                .message("Успешно покинули сообщество")
                .build());
    }

    @Override
    @GetMapping("/{communityId}/members")
    @Operation(summary = "Получить участников сообщества", description = "Возвращает список участников сообщества")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список участников успешно получен"),
        @ApiResponse(responseCode = "404", description = "Сообщество не найдено")
    })
    public ResponseEntity<?> getCommunityMembers(Long communityId) {
        List<UserDto> members = communityService.getCommunityMembers(communityId);
        return ResponseEntity.ok(SuccessResponse.builder()
                .success(true)
                .message("Список участников успешно получен")
                .data(members)
                .build());
    }

    @Override
    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить сообщества пользователя", description = "Возвращает список сообществ пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список сообществ успешно получен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<?> getCommunitiesByUser(Long userId) {
        List<CommunityDto> communities = communityService.getUserCommunities(userId);
        return ResponseEntity.ok(SuccessResponse.builder()
                .success(true)
                .message("Список сообществ успешно получен")
                .data(communities)
                .build());
    }

}
