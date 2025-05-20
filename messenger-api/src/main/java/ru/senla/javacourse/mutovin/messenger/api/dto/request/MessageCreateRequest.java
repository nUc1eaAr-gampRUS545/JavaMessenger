package ru.senla.javacourse.mutovin.messenger.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Запрос на создание сообщения")
public class MessageCreateRequest {
    @NotNull
    @Schema(description = "ID чата", example = "1", required = true)
    private Long chatId;
    
    @NotBlank
    @Schema(description = "Текст сообщения", example = "Привет, как дела?", required = true)
    private String content;
} 