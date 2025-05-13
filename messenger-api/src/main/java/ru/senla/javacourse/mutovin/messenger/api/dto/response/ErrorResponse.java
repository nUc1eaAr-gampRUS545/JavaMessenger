package ru.senla.javacourse.mutovin.messenger.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ при ошибке запроса")
public class ErrorResponse<T> {

    @Schema(description = "Флаг ошибки", example = "false")
    private boolean success;

    @Schema(description = "Код ошибки", example = "400")
    private int status;

    @Schema(description = "Описание ошибки", example = "Некорректные данные")
    private String message;

    @Schema(description = "Детали ошибки (если есть)")
    private T details;

}
