package ru.senla.javacourse.mutovin.messenger.impl.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.senla.javacourse.mutovin.messenger.api.dto.CommunityDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.PostDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.Community;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.CommunityMapper;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.PostMapper;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommunityMapperImpl implements CommunityMapper {

    private final UserMapper userMapper;
    private final PostMapper postMapper;

    @Override
    public CommunityDto map(Community entity) {

        if (entity==null) return null;

        CommunityDto dto = new CommunityDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        return dto;
    }
}
