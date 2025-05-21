package ru.senla.javacourse.mutovin.messenger.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.senla.javacourse.mutovin.messenger.api.dto.ChatParticipantDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.ChatParticipant;

@Mapper(componentModel = "spring")
public interface ChatParticipantMapper {

    @Mapping(target = "id", ignore = true)
    ChatParticipantDto map( ChatParticipant source);
}
