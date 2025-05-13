package ru.senla.javacourse.mutovin.messenger.impl.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.senla.javacourse.mutovin.messenger.db.entity.Friendship;
import ru.senla.javacourse.mutovin.messenger.impl.repository.FriendshipRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class FriendshipRepositoryImpl implements FriendshipRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Friendship> findByUserId(Long userId) {
        TypedQuery<Friendship> query = entityManager.createQuery(
                "SELECT f FROM Friendship f WHERE f.user.id = :userId", Friendship.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public Optional<Friendship> findByUserIdAndFriendId(Long userId, Long friendId) {
        TypedQuery<Friendship> query = entityManager.createQuery(
                "SELECT f FROM Friendship f WHERE f.user.id = :userId AND f.friend.id = :friendId", 
                Friendship.class);
        query.setParameter("userId", userId);
        query.setParameter("friendId", friendId);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public void deleteByUserIdAndFriendId(Long userId, Long friendId) {
        entityManager.createQuery(
                "DELETE FROM Friendship f WHERE f.user.id = :userId AND f.friend.id = :friendId")
                .setParameter("userId", userId)
                .setParameter("friendId", friendId)
                .executeUpdate();
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return Optional.of(entity);
        }
        return Optional.of(entityManager.merge(entity));
    }

    @Override
    public Optional<Friendship> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Friendship.class, id));
    }

    @Override
    public void deleteById(Long id) {
        Friendship friendship = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Friendship not found"));
        entityManager.remove(friendship);
    }

    @Override
    public Optional<List<Friendship>> findAll() {
        TypedQuery<Friendship> query = entityManager.createQuery(
                "SELECT f FROM Friendship f", Friendship.class);
        return Optional.of(query.getResultList());
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }
} 