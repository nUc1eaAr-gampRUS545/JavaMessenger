package ru.senla.javacourse.mutovin.messenger.api.dto;

import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class ChatDto implements Serializable {
    private Long id;
    private String name;
    private Boolean isPrivate;
    private UserDto creator;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ChatParticipantDto> participants;
    private List<MessageDto> messages;
}
