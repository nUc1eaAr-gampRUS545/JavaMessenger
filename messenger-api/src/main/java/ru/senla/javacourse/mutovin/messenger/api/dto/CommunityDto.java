package ru.senla.javacourse.mutovin.messenger.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class CommunityDto {
    private Long id;
    private String name;
    private String description;
    private List<UserDto> members;
    private List<PostDto> posts;
}
