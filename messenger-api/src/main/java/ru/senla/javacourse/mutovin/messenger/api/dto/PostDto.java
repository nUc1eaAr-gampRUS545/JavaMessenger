package ru.senla.javacourse.mutovin.messenger.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class PostDto {
    private Long id;
    private UserDto creator;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private PostStatus status;

}

