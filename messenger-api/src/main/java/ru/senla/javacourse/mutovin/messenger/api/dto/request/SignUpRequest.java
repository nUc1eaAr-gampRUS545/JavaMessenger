package ru.senla.javacourse.mutovin.messenger.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.HashSet;

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

    @Schema(description = "Пол пользователя", example = "MALE/FEMALE")
    @NotBlank(message = "Пол пользователя не может быть пустым")
    @Pattern(regexp = "MALE|FEMALE", message = "Пол пользователя должен быть MALE или FEMALE")
    private String gender;

    @Schema(description = "Возраст пользователя", example = "20")
    @Min(value = 8, message = "Возраст пользователя должен быть от 8 лет")
    @Max(value = 140, message = "Возраст пользователя должен быть до 140 лет")
    private Integer age;

    @Schema(description = "Телефон пользователя", example = "+7(999)999-99-99")
    @Size(min = 8, max = 50, message = "Телефон пользователя должно содержать от 5 до 50 символов")
    @NotBlank(message = "Телефон пользователя не может быть пустыми")
    private String phonenumber;

    @Schema(description = "Логин пользователя", example = "JonSnow2017")
    @Size(min = 5, max = 50, message = "Логин пользователя должно содержать от 5 до 50 символов")
    @NotBlank(message = "Логин пользователя не может быть пустыми")
    private String username;

    @Schema(description = "Адрес электронной почты", example = "jonSnow@gmail.com")
    @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустыми")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String email;

    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(min=8,max = 255, message = "Длина пароля должна быть не более 255 символов")
    private String password;

//    public User toEntity(){
//        User user = User.builder()
//                .firstname(firstname)
//                .lastname(lastname)
//                .phoneNumber(phonenumber)
//                .username(username)
//                .email(email)
//                .age(age)
//                .gender(Gender.valueOf(gender))
//                .role(Role.ROLE_USER)
////                .communities(new HashSet<>())
//                .build();
//        return user;
//    }

}