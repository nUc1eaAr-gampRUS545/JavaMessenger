package ru.senla.javacourse.mutovin.messenger.impl.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.senla.javacourse.mutovin.messenger.api.dto.MessageDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.Message;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.MessageMapper;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.UserMapper;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class MessageMapperImpl implements MessageMapper {

    private final UserMapper userMapper;
    @Override
    public MessageDto map(Message entity) {
        if(entity == null) return null;

        UserDto userDto = userMapper.map(entity.getSender());

        MessageDto dto = new MessageDto();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setSender(userDto);
        return dto;

    }

}
