package ru.senla.javacourse.mutovin.messenger.impl.mapper.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.senla.javacourse.mutovin.messenger.api.dto.FriendRequestDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.FriendRequest;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.FriendRequestMapper;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.UserMapper;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
@Data
public class FriendRequestMapperImpl implements FriendRequestMapper {

    private final UserMapper userMapper;

    @Override
    public FriendRequestDto map(FriendRequest entity) {

        UserDto recipientDto = userMapper.map(entity.getRecipient());
        UserDto senderDto = userMapper.map(entity.getSender());

        FriendRequestDto dto = new FriendRequestDto();
        dto.setId(entity.getId());
        dto.setRecipient(recipientDto);
        dto.setSender(senderDto);
        dto.setStatus(String.valueOf(entity.getStatus()));
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
