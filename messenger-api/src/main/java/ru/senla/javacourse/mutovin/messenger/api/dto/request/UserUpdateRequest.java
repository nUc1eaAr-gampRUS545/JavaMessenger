package ru.senla.javacourse.mutovin.messenger.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на обновление профиля пользователя")
public class UserUpdateRequest {

    @Schema(description = "Имя пользователя", example = "Jon")
    @Size(min = 3, max = 50, message = "Имя пользователя должно содержать от 3 до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String firstname;

    @Schema(description = "Фамилия пользователя", example = "Snow")
    @Size(min = 3, max = 50, message = "Фамилия пользователя должна содержать от 3 до 50 символов")
    @NotBlank(message = "Фамилия пользователя не может быть пустой")
    private String lastname;

    @Schema(description = "Пол пользователя", example = "MALE/FEMALE")
    @NotBlank(message = "Пол пользователя не может быть пустым")
    @Pattern(regexp = "MALE|FEMALE", message = "Пол пользователя должен быть MALE или FEMALE")
    private String gender;

    @Schema(description = "Возраст пользователя", example = "20")
    @Min(value = 8, message = "Возраст пользователя должен быть не меньше 8 лет")
    @Max(value = 140, message = "Возраст пользователя должен быть не больше 140 лет")
    private Integer age;

    @Schema(description = "Телефон пользователя", example = "+7(999)999-99-99")
    @Size(min = 8, max = 50, message = "Телефон пользователя должен содержать от 8 до 50 символов")
    @NotBlank(message = "Телефон пользователя не может быть пустым")
    private String phonenumber;

    @Schema(description = "Адрес электронной почты", example = "jonSnow@gmail.com")
    @Size(min = 5, max = 255, message = "Email должен содержать от 5 до 255 символов")
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен быть в формате user@example.com")
    private String email;
}
