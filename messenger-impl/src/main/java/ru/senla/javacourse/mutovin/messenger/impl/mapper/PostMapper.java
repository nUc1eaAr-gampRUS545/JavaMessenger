package ru.senla.javacourse.mutovin.messenger.impl.mapper;

import ru.senla.javacourse.mutovin.messenger.api.dto.PostDto;
import ru.senla.javacourse.mutovin.messenger.api.dto.request.PostCreateRequest;
import ru.senla.javacourse.mutovin.messenger.db.entity.Post;

public interface PostMapper extends GenericMapper<Post, PostDto> {
}
