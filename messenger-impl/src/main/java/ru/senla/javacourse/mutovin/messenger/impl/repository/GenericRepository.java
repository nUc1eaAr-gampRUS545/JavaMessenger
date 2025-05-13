package ru.senla.javacourse.mutovin.messenger.impl.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, PK extends Serializable>{
    Optional<T> save(T entity);
    Optional<T> findById(PK primaryKey);
    Optional<List<T>> findAll();
    void deleteById(PK primaryKey);
    boolean existsById(Long primaryKey);
}
