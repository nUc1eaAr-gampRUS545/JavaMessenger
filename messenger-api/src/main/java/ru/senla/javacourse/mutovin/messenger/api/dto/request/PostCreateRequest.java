package ru.senla.javacourse.mutovin.messenger.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание поста")
public class PostCreateRequest implements Serializable {

    @NotBlank(message = "Заголовок поста не может быть пустым")
    @Size(min = 1, max = 100, message = "Заголовок должен содержать от 1 до 100 символов")
    @Schema(description = "Заголовок поста", example = "Мой первый пост", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "Содержимое поста не может быть пустым")
    @Size(min = 1, max = 5000, message = "Содержимое должно содержать от 1 до 5000 символов")
    @Schema(description = "Содержимое поста", example = "Это содержимое моего первого поста...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

//    public Post toEntity(){
//        Post post = new Post();
//        post.setContent(content);
//        post.setCreatedAt(LocalDateTime.now());
//        post.setTitle(title);
//        post.setStatus(PostStatus.PUBLISHED);
//        return post;
//    }
}
