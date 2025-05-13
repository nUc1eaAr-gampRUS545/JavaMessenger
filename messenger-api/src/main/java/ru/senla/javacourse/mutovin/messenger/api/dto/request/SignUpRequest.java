package ru.senla.javacourse.mutovin.messenger.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на регистрацию")
public class SignUpRequest {

    @Schema(description = "Имя пользователя", example = "Jon")
    @Size(min = 3, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String firstname;

    @Schema(description = "Фамилия пользователя", example = "Snow")
    @Size(min = 3, max = 50, message = "Фамилия пользователя должно содержать от 5 до 50 символов")
    @NotBlank(message = "Фамилия пользователя не может быть пустыми")
    private String lastname;

    @Schema(description = "Телефон пользователя", example = "+7(999)999-99-99")
    @Size(min = 8, max = 50, message = "Телефон пользователя должно содержать от 5 до 50 символов")
    @NotBlank(message = "Телефон пользователя не может быть пустыми")
    private String phonenumber;

    @Schema(description = "Логин пользователя", example = "JonSnow2028")
    @Size(min = 5, max = 50, message = "Логин пользователя должно содержать от 5 до 50 символов")
    @NotBlank(message = "Логин пользователя не может быть пустыми")
    private String username;

    @Schema(description = "Адрес электронной почты", example = "jondoe@gmail.com")
    @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустыми")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String email;

    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(min=8,max = 255, message = "Длина пароля должна быть не более 255 символов")
    private String password;
}