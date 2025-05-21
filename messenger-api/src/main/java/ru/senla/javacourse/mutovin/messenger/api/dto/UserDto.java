package ru.senla.javacourse.mutovin.messenger.api.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
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
    private List<UserDto> friends;
    private List<PostDto> posts;
}
