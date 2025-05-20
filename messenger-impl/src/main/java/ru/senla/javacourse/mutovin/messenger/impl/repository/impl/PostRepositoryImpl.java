package ru.senla.javacourse.mutovin.messenger.impl.repository.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.senla.javacourse.mutovin.messenger.db.entity.Message;
import ru.senla.javacourse.mutovin.messenger.db.entity.Post;
import ru.senla.javacourse.mutovin.messenger.impl.repository.PostRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.openSession();
    }

    @Override
    public Optional<List<Post>> findByCreatorId(Long creatorId) {

        try (Session session = getSession()) {
            String hql = "FROM Post p JOIN FETCH p.creator JOIN FETCH p.creator WHERE p.creator.id = :creatorId ORDER BY p.createdAt DESC";
            Query<Post> query = session.createQuery(hql,Post.class)
                    .setParameter("creatorId",creatorId);
            return Optional.ofNullable(query.list());
        }

    }

    @Override
    public Optional<Post> update(Post post) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            Post result = session.merge(post);
            transaction.commit();
            return Optional.ofNullable(result);
        }

    }

    @Override
    public Optional<Post> save(Post entity) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            Post post = session.merge(entity);
            transaction.commit();
            return Optional.ofNullable(post);
        }
    }

    @Override
    public Optional<Post> findById(Long primaryKey) {
        try (Session session = getSession()) {
            Post post = session.find(Post.class,primaryKey);
            return Optional.ofNullable(post);
        }

    }

    @Override
    public Optional<List<Post>> findAll() {
        try (Session session = getSession()) {
            return Optional.ofNullable(session.createQuery("from Post",Post.class).list());
        }
    }

    @Override
    public void deleteById(Long primaryKey) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            Optional<Post> post = findById(primaryKey);
            post.ifPresent(session::remove);
            transaction.commit();
        }
    }

    @Override
    public boolean existsById(Long primaryKey) {
        return false;
    }
}
