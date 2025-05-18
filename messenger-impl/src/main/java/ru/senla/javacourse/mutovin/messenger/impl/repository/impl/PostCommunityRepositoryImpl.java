package ru.senla.javacourse.mutovin.messenger.impl.repository.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.senla.javacourse.mutovin.messenger.db.entity.PostCommunity;
import ru.senla.javacourse.mutovin.messenger.impl.repository.PostCommunityRepository;

import java.util.List;
import java.util.Optional;
@Repository
@RequiredArgsConstructor

public class PostCommunityRepositoryImpl implements PostCommunityRepository {

    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.openSession();
    }

    @Override
    public List<PostCommunity> findByUserId(Long communityId) {
        try (Session session = getSession()) {
            Query<PostCommunity> query = session.createQuery(
                    "SELECT pc FROM PostCommunity pc WHERE pc.community.id = :communityId", PostCommunity.class);
            query.setParameter("communityId", communityId);
            return query.list();
        }
    }

    @Override
    public void deleteByPostIdAndCommunityId(Long postId,Long communityId) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            Query<?> query = session.createQuery(
                    "DELETE FROM PostCommunity pc WHERE pc.post.id = :postId AND pc.community.id = :communityId");
            query.setParameter("postId", postId);
            query.setParameter("communityId", communityId);
            query.executeUpdate();
            transaction.commit();
        }
    }

    @Override
    public Optional<PostCommunity> save(PostCommunity entity) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            PostCommunity pc = session.merge(entity);
            transaction.commit();
            return Optional.of(pc);
        }
    }

    @Override
    public Optional<PostCommunity> findById(Long id) {
        try (Session session = getSession()) {
            return Optional.ofNullable(session.get(PostCommunity.class, id));
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            PostCommunity postInCommunity = findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Пост сообщества не найден"));
            session.remove(postInCommunity);
            transaction.commit();
        }
    }

    @Override
    public Optional<List<PostCommunity>> findAll() {
        try (Session session = getSession()) {
            Query<PostCommunity> query = session.createQuery(
                    "SELECT pc FROM PostCommunity pc", PostCommunity.class);
            return Optional.of(query.list());
        }
    }

    @Override
    public boolean existsById(Long id) {
        try (Session session = getSession()) {
            Query<?> query = session.createQuery(
                    "SELECT COUNT(pc) FROM PostCommunity pc WHERE pc.id = :id");
            query.setParameter("id", id);
            Long count = (Long) query.uniqueResult();
            return count > 0;
        }
    }
}
