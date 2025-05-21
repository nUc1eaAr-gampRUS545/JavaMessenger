package ru.senla.javacourse.mutovin.messenger.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.senla.javacourse.mutovin.messenger.api.dto.CommunityDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.CommunityCreateRequest;
import ru.senla.javacourse.mutovin.messenger.db.entity.Community;

@Mapper(componentModel = "spring")
public interface CommunityMapper {

    @Mapping(target = "id", ignore = true)
    CommunityDto map(Community source);
    Community toEntity(CommunityCreateRequest source);
}
