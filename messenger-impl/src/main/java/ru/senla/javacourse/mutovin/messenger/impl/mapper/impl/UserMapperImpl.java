package ru.senla.javacourse.mutovin.messenger.impl.mapper.impl;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.senla.javacourse.mutovin.messenger.api.dto.UserDto;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;
import ru.senla.javacourse.mutovin.messenger.impl.mapper.UserMapper;

import java.io.Serializable;

@Component
@Data
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto map(User user) {

        if(user == null) return null;

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstname(user.getFirstname());
        userDto.setLastname(user.getLastname());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        userDto.setPhoneNumber(user.getPhoneNumber());
        return userDto;
    }



}

