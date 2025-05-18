package ru.senla.javacourse.mutovin.messenger.api.dto;

import lombok.Data;

import ru.senla.javacourse.mutovin.messenger.db.entity.MessageStatus;
import ru.senla.javacourse.mutovin.messenger.db.entity.PostStatus;

import java.time.LocalDateTime;

@Data
public class PostDto {

    private Long id;
    private UserDto creator;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private PostStatus status;

}

