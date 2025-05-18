package ru.senla.javacourse.mutovin.messenger.api.dto;

import lombok.Data;
import ru.senla.javacourse.mutovin.messenger.db.entity.Gender;
import ru.senla.javacourse.mutovin.messenger.db.entity.Role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserDto implements Serializable {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private Integer age;
    private Gender gender;
    private String email;
    private String phoneNumber;
    private Role role;
    private List<UserDto> friends = new ArrayList<>();
    private List<PostDto> posts = new ArrayList<>();
}
