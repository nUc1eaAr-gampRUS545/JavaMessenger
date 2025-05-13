package ru.senla.javacourse.mutovin.messenger.impl.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.senla.javacourse.mutovin.messenger.api.dto.MessageDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.PostDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostCreateRequest;
import ru.senla.javacourse.mutovin.messenger.db.entity.Message;
import ru.senla.javacourse.mutovin.messenger.db.entity.MessageStatus;
import ru.senla.javacourse.mutovin.messenger.db.entity.Post;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.PostMapper;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.UserMapper;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PostMapperImpl implements PostMapper {

    private final UserMapper userMapper;

    @Override
    public PostDto map(Post entity) {
        if(entity == null) return null;

        UserDto userDto = userMapper.map(entity.getCreator());

        PostDto dto = new PostDto();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCreator(userDto);
        return dto;

    }

}
