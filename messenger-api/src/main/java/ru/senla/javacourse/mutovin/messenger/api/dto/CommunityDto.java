package ru.senla.javacourse.mutovin.messenger.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class CommunityDto {
    private Long id;
    private String name;
    private String description;
    private List<UserDto> members;
    private List<PostDto> posts;
}
