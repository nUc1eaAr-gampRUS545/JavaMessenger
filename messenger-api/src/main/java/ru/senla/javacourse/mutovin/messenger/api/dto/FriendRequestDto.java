package ru.senla.javacourse.mutovin.messenger.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FriendRequestDto implements Serializable {
    private Long id;
    private UserDto sender;
    private UserDto recipient;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum MessageStatus {
        SENT,
        DELIVERED,
        READ,
        EDITED,
        DELETED,
        DELETED_BY_ADMIN
    }
}