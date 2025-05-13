package ru.senla.javacourse.mutovin.messenger.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ChatParticipantDto implements Serializable {
    private Long id;
    private UserDto user;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private Boolean isAdmin;
}
