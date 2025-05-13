package ru.senla.javacourse.mutovin.messenger.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "Запрос на создание группового чата")
public class ChatCreateRequest {
    @NotBlank
    @Schema(description = "Название чата", example = "Общий чат", required = true)
    private String name;
    
    @NotNull
    @Schema(description = "ID создателя чата", example = "1", required = true)
    private Long creatorId;
    
    @NotNull
    @Schema(description = "Список ID участников чата", example = "[1, 2, 3]", required = true)
    private Set<Long> participantIds;
} 