package ru.senla.javacourse.mutovin.messenger.impl.repository.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.senla.javacourse.mutovin.messenger.db.entity.Subscribe;
import ru.senla.javacourse.mutovin.messenger.impl.repository.SubscribeRepository;

import java.util.List;
import java.util.Optional;
@Repository
@RequiredArgsConstructor
public class SubscribeRepositoryImpl implements SubscribeRepository {

    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.openSession();
    }

    @Override
    public List<Subscribe> findByUserId(Long userId) {
        try (Session session = getSession()) {
            Query<Subscribe> query = session.createQuery(
                    "SELECT c FROM Subscribe c WHERE c.member.id = :userId", Subscribe.class);
            query.setParameter("userId", userId);
            return query.list();
        }
    }

    @Override
    public void deleteByUserIdAndCommunityId(Long userId,Long communityId) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            Query<?> query = session.createQuery(
                    "DELETE FROM Subscribe s WHERE s.member.id = :userId AND s.community.id = :communityId");
            query.setParameter("userId", userId);
            query.setParameter("communityId", communityId);
            query.executeUpdate();
            transaction.commit();
        }
    }

    @Override
    public Optional<Subscribe> save(Subscribe entity) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            Subscribe result = session.merge(entity);
            transaction.commit();
            return Optional.of(result);
        }
    }

    @Override
    public Optional<Subscribe> findById(Long id) {
        try (Session session = getSession()) {
            return Optional.ofNullable(session.get(Subscribe.class, id));
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            Subscribe subscribe = findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Подписка не найдена"));
            session.remove(subscribe);
            transaction.commit();
        }
    }

    @Override
    public Optional<List<Subscribe>> findAll() {
        try (Session session = getSession()) {
            Query<Subscribe> query = session.createQuery(
                    "SELECT s FROM Subscribe s", Subscribe.class);
            return Optional.of(query.list());
        }
    }

    @Override
    public boolean existsById(Long id) {
        try (Session session = getSession()) {
            Query<?> query = session.createQuery(
                    "SELECT COUNT(f) FROM Friendship f WHERE f.id = :id");
            query.setParameter("id", id);
            Long count = (Long) query.uniqueResult();
            return count > 0;
        }
    }
}
