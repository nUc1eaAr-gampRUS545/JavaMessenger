package ru.senla.javacourse.mutovin.messenger.impl.mapper;

import java.io.Serializable;

public interface GenericMapper<T, EntityDto>  {
    EntityDto map(T entity);
}
