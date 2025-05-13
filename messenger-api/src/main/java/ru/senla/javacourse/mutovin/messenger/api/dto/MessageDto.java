package ru.senla.javacourse.mutovin.messenger.api.dto;

import lombok.Data;
import ru.senla.javacourse.mutovin.messenger.db.entity.MessageStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
public class MessageDto implements Serializable {

    private Long id;
    private UserDto sender;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime readAt;
    private MessageStatus status;
}

