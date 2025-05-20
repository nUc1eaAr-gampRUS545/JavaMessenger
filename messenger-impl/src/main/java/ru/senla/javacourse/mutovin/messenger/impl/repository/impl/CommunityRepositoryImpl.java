package ru.senla.javacourse.mutovin.messenger.impl.repository.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.senla.javacourse.mutovin.messenger.db.entity.Community;
import ru.senla.javacourse.mutovin.messenger.db.entity.Post;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;
import ru.senla.javacourse.mutovin.messenger.impl.repository.CommunityRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommunityRepositoryImpl implements CommunityRepository {

    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.openSession();
    }

    @Override
    public Optional<List<Community>> getCommunitiesByUserId(Long userId) {
        try (Session session = getSession()) {
            return Optional.of(session.createQuery(
                            "SELECT s.community FROM Subscribe s " +
                                    "LEFT JOIN s.member m " +
                                    "WHERE m.id = :userId",
                            Community.class)
                    .setParameter("userId",userId)
                    .list());
        }
    }

    @Override
    public Optional<List<User>> getMembersByCommunityId(Long communityId) {
        try (Session session = getSession()) {
            return Optional.ofNullable(session.createQuery(
                            "SELECT m FROM Subscribe s " +
                                    "LEFT JOIN s.member m " +
                                    "LEFT JOIN s.community c " +
                                    "WHERE c.id = :communityId",
                            User.class)
                    .setParameter("communityId",communityId)
                    .list());
        }
    }

    @Override
    public Optional<List<Post>> getPostsInCommunity(Long communityId) {
        try (Session session = getSession()) {
            List<Post> posts = session.createQuery(
                            "SELECT pc.post FROM PostCommunity pc " +
                                    "JOIN pc.community c " +
                                    "WHERE c.id = :communityId",Post.class)
                    .setParameter("communityId",communityId)
                    .list();
            return Optional.of(posts);
        }
    }

    @Override
    public boolean isMember(Long userId,Long communityId) {
        try (Session session = getSession()) {
            Query<Long> query = session.createQuery(
                            "SELECT COUNT(*) FROM Subscribe s " +
                                    "JOIN s.member m " +
                                    "JOIN s.community c " +
                                    "WHERE c.id = :communityId " +
                                    "AND m.id = :userId",Long.class)
                    .setParameter("communityId",communityId)
                    .setParameter("userId",userId);

            return query.uniqueResult() > 0;
        }
    }

    @Override
    public Optional<List<Community>> findAll() {
        try (Session session = getSession()) {
            return Optional.ofNullable(session.createQuery("FROM Community",Community.class).list());
        }
    }

    @Override
    public Optional<Community> findById(Long id) {
        try (Session session = getSession()) {
            Community community = session.get(Community.class,id);

            return Optional.ofNullable(community);
        }
    }

    @Override
    public Optional<Community> update(Community community) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(community);
            transaction.commit();
        }
        return Optional.ofNullable(community);
    }

    @Override
    public Optional<Community> save(Community community) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(community);
            transaction.commit();
            return Optional.ofNullable(community);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            Community community = session.get(Community.class,id);
            if (community!=null) {
                session.delete(community);
            }
            transaction.commit();
        }
    }

    @Override
    public boolean existsById(Long primaryKey) {
        return false;
    }


}
