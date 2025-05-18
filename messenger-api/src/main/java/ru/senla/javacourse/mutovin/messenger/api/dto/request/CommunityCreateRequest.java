package ru.senla.javacourse.mutovin.messenger.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.senla.javacourse.mutovin.messenger.db.entity.Community;

import java.time.LocalDateTime;
import java.util.HashSet;

@Data
@Schema(description = "Запрос на создание сообщества")
public class CommunityCreateRequest {
    @NotBlank(message = "Название сообщества не может быть пустым")
    @Size(min = 2, max = 50, message = "Название должно быть от 2 до 50 символов")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 2, max = 200, message = "Описание должно быть от 2 до 200 символов")
    private String description;

    public Community toEntity() {
        Community community = new Community();
        community.setName(name);
        community.setDescription(description);
        community.setCreatedAt(LocalDateTime.now());
        community.setUpdatedAt(LocalDateTime.now());
        return community;
    }
}
