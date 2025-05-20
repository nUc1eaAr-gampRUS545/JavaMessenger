package ru.senla.javacourse.mutovin.messenger.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapping;

import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.SignUpRequest;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.UserUpdateRequest;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    UserDto map(User source);

    User toEntity(SignUpRequest source);
}
