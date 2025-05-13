package ru.senla.javacourse.mutovin.messenger.impl.mapper.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.senla.javacourse.mutovin.messenger.api.dto.ChatParticipantDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.ChatParticipant;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.ChatParticipantMapper;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.UserMapper;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
@Data
public class ChatParticipantMapperImpl implements ChatParticipantMapper {

    private final UserMapper userMapper;
    @Override
    public ChatParticipantDto map(ChatParticipant entity) {
        if(entity == null) return null;

        UserDto userDto = userMapper.map(entity.getUser());
        ChatParticipantDto dto = new ChatParticipantDto();
        dto.setId(entity.getId());
        dto.setJoinedAt(entity.getJoinedAt());
        dto.setIsAdmin(entity.getIsAdmin());
        dto.setLeftAt(entity.getLeftAt());
        dto.setUser(userDto);
        return dto;
    }

}
