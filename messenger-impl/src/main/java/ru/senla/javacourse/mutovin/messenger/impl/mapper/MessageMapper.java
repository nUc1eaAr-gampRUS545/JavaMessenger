package ru.senla.javacourse.mutovin.messenger.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.senla.javacourse.mutovin.messenger.api.dto.MessageDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.Message;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "id", ignore = true)
    MessageDto map(Message source);
}
