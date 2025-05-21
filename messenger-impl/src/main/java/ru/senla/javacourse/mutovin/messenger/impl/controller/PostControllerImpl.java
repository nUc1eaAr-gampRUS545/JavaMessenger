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
import ru.senla.javacourse.mutovin.messenger.api.controller.PostController;
import ru.senla.javacourse.mutovin.messenger.api.dto.PostDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostCreateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostUpdateRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.ErrorResponse;
import ru.senla.javacourse.mutovin.messenger.api.dto.response.SuccessResponse;
import ru.senla.javacourse.mutovin.messenger.db.entity.Role;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;

import ru.senla.javacourse.mutovin.messenger.impl.service.PostService;
import ru.senla.javacourse.mutovin.messenger.impl.service.UserService;

import java.util.List;
import java.util.Objects;

@Tag(name = "Посты", description = "API для управления постами")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostControllerImpl implements PostController {

    private final PostService postService;
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(PostControllerImpl.class);

    @Operation(summary = "Создать пост", description = "Создает новый пост")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пост успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")})
    @PostMapping
    public ResponseEntity<?> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PostCreateRequest request) {

        if (request==null || request.getContent()==null || request.getContent().isBlank()) {
            return ResponseEntity.badRequest().body(ErrorResponse.builder().success(false).
                    status(HttpStatus.BAD_REQUEST.value()).message("Содержимое поста не может быть пустым")
                    .build());
        }
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        PostDto post = postService.createPost(request,userId);
        return ResponseEntity.ok(SuccessResponse.builder().success(true).message("Пост успешно создан")
                .data(post).build());

    }

    @Operation(summary = "Получить пост по ID", description = "Возвращает пост по его идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пост успешно получен"),
            @ApiResponse(responseCode = "404", description = "Пост не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")})
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostById(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID поста") @PathVariable Long postId) {

        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        PostDto post = postService.findPostById(postId,userId);
        return ResponseEntity.ok(SuccessResponse.builder().success(true).message("Пост успешно получен")
                .data(post).build());

    }

    @Operation(summary = "Получить посты пользователя",
            description = "Возвращает все посты указанного пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список постов успешно получен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")})
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPostsByUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID пользователя") @PathVariable Long userId) {
        userService.findByUsername(userDetails.getUsername());
        List<PostDto> posts = postService.findPostByCreatorId(userId);
        return ResponseEntity.ok(SuccessResponse.builder().success(true)
                .message("Посты пользователя успешно получены").data(posts).build());

    }

    @Operation(summary = "Получить все посты", description = "Возвращает все посты в системе")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Список постов успешно получен")})
    @GetMapping
    public ResponseEntity<?> getAllPosts(@AuthenticationPrincipal UserDetails userDetails) {

        userService.findByUsername(userDetails.getUsername());
        List<PostDto> posts = postService.findAllPosts();
        return ResponseEntity.ok(SuccessResponse.builder().success(true).message("Все посты успешно получены")
                .data(posts).build());

    }

    @Operation(summary = "Обновить пост", description = "Обновляет содержимое поста")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Пост успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Пост не найден")})
    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID поста") @PathVariable Long postId,
            @RequestBody PostUpdateRequest request) {

        if (request==null || request.getContent()==null || request.getContent().isBlank()) {
            return ResponseEntity.badRequest().body(ErrorResponse.builder().success(false)
                    .status(HttpStatus.BAD_REQUEST.value()).message("Содержимое поста не может быть пустым")
                    .build());
        }

        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        PostDto updatedPost = postService.updatePost(request,userId);

        return ResponseEntity.ok(SuccessResponse.builder().success(true).message("Пост успешно обновлен")
                .data(updatedPost).build());

    }

    @Operation(summary = "Удалить пост (администратор)",
            description = "Удаляет пост по его ID (только для администраторов)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пост успешно удален"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Пост не найден")
    })
    @DeleteMapping("/admin/{postId}")
    public ResponseEntity<?> deletePost(@AuthenticationPrincipal UserDetails userDetails,
                                        @Parameter(description = "ID поста") @PathVariable Long postId) {
        User person = userService.findByUsername(userDetails.getUsername());
        if (person.getRole().equals(Role.ROLE_ADMIN)) {
            postService.adminDeletePostById(postId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Operation(summary = "Удалить пост", description = "Удаляет пост по его идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пост успешно удален"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Пост не найден")})
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deleteMyPost(@AuthenticationPrincipal UserDetails userDetails,
                                          @Parameter(description = "ID поста") @PathVariable Long postId) {
        User person = userService.findByUsername(userDetails.getUsername());
        PostDto post = postService.findPostById(postId,person.getId());
        if (Objects.equals(post.getCreator().getId(),person.getId())) {
            postService.deletePostById(postId,person.getId());
            return ResponseEntity.ok().build();
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

    }

}