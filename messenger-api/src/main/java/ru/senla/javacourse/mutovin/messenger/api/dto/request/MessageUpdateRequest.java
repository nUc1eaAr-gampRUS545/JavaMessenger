package ru.senla.javacourse.mutovin.messenger.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Schema(description = "Запрос на обновление сообщения")
@Builder(toBuilder = true)
public class MessageUpdateRequest {
    @NotBlank
    @Schema(description = "Новый текст сообщения", example = "Измененное сообщение", required = true)
    private String content;
} 