package ru.senla.javacourse.mutovin.messenger.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.senla.javacourse.mutovin.messenger.api.dto.ChatDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.Chat;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    ChatMapper INSTANCE = Mappers.getMapper(ChatMapper.class);

    @Mapping(target = "id", ignore = true)
    ChatDto map(Chat source);
}
