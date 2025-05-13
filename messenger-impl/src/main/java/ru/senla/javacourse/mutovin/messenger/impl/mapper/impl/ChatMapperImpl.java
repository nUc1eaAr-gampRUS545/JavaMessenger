package ru.senla.javacourse.mutovin.messenger.impl.mapper.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.senla.javacourse.mutovin.messenger.api.dto.ChatDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.ChatParticipantDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.Chat;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.ChatMapper;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.ChatParticipantMapper;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.UserMapper;

import java.io.Serializable;
import java.util.List;

@Component
@Data
@RequiredArgsConstructor
public class ChatMapperImpl implements ChatMapper {

    private final UserMapper userMapper;
    private final ChatParticipantMapper chatParticipantMapper;

    @Override
    public ChatDto map(Chat entity) {
        if(entity == null) return null;

        UserDto user = userMapper.map(entity.getCreator());

        List<ChatParticipantDto> participants =
                entity.getParticipants().stream()
                .map(chatParticipantMapper::map)
                .toList();

        ChatDto chatDto = new ChatDto();
        chatDto.setId(entity.getId());
        chatDto.setName(entity.getName());
        chatDto.setCreatedAt(entity.getCreatedAt());
        chatDto.setUpdatedAt(entity.getUpdatedAt());
        chatDto.setCreator(user);
        chatDto.setIsPrivate(entity.getIsPrivate());
        chatDto.setParticipants(participants);
        return chatDto;
    }

}
