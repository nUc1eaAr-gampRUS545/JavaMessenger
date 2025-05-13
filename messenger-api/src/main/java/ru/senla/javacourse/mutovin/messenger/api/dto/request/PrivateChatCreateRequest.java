package ru.senla.javacourse.mutovin.messenger.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Запрос на создание приватного чата")
public class PrivateChatCreateRequest {
    @NotNull
    @Schema(description = "ID первого участника", example = "1", required = true)
    private Long firstUserId;

    @NotNull
    @Schema(description = "ID второго участника", example = "2", required = true)
    private Long secondUserId;
} 