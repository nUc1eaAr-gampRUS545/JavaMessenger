package ru.senla.javacourse.mutovin.messenger.impl.repository.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import org.hibernate.query.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.javacourse.mutovin.messenger.db.entity.User;
import ru.senla.javacourse.mutovin.messenger.impl.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.openSession();
    }

    @Override
    @Transactional
    public Optional<User> save(User user) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();

        }
        return Optional.ofNullable(user);
    }
    @Override
    @Transactional
    public Optional<User> update(User user) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();

        }
        return Optional.ofNullable(user);
    }

    @Override
    @Transactional
    public Optional<User> findByUsername(String username) {
        try (Session session = getSession()) {
            String hql = "FROM User u WHERE u.username = :username";
            Query<User> query = session.createQuery(hql, User.class).setParameter("username", username);
            return query.uniqueResultOptional();
        }

    }

    @Override
    @Transactional
    public Optional<User> findById(Long id) {
        try (Session session = getSession()) {
            return Optional.ofNullable(session.find(User.class,id));
        }

    }

    @Override
    @Transactional
    public Optional<List<User>> findAll() {
        try (Session session = getSession()){
            return Optional.of(session.createQuery("from User").stream().toList());
        }
    }

    @Override
    @Transactional
    public Optional<Set<User>> findAllByUserIds(Set<Long> userIds) {
        Set<User> users = new HashSet<>();
        userIds.forEach(userId -> users.add(getById(userId)));
        return Optional.of(users);
    }

    @Override
    @Transactional
    public boolean existsByUsername(String username) {
        try (Session session = getSession()) {
            String hql = "SELECT count(u) FROM User u WHERE u.username = :username";
            Query<Long> query = session.createQuery(hql,Long.class).setParameter("username", username);
            return query.uniqueResult() > 0;
        }
    }

    @Override
    @Transactional
    public boolean existsByEmail(String email) {
        try (Session session = getSession()) {
            String hql = "SELECT count(u) FROM User u WHERE u.email = :email";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("email", email);
            return query.uniqueResult() > 0;
        }

    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            Optional<User> user = findById(id);
            if (user.isPresent()) {
                session.remove(user);
                transaction.commit();
            }
        }
    }

    @Override
    @Transactional
    public boolean existsById(Long primaryKey) {
        try(Session session = getSession()){
            User entity = session.get(User.class, primaryKey);
            return entity != null;
        }
    }

    private User getById(Long id) {
        try (Session session = getSession()) {
            return session.find(User.class,id);
        }

    }
}

