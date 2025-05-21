package ru.senla.javacourse.mutovin.messenger.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.senla.javacourse.mutovin.messenger.api.dto.PostDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostCreateRequest;
import ru.senla.javacourse.mutovin.messenger.db.entity.Post;


@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "id", ignore = true)
    PostDto map(Post source);

    Post toEntity(PostCreateRequest source);

}
