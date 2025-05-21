package ru.senla.javacourse.mutovin.messenger.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.senla.javacourse.mutovin.messenger.api.dto.FriendRequestDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.FriendRequest;

@Mapper(componentModel = "spring")
public interface FriendRequestMapper  {

    @Mapping(target = "id", ignore = true)
    FriendRequestDto map(FriendRequest source);
}
