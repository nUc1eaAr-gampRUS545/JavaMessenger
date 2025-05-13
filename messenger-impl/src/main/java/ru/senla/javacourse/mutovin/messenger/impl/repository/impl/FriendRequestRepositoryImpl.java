package ru.senla.javacourse.mutovin.messenger.impl.repository.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.senla.javacourse.mutovin.messenger.db.entity.FriendRequest;
import ru.senla.javacourse.mutovin.messenger.db.entity.Message;
import ru.senla.javacourse.mutovin.messenger.impl.repository.FriendRequestRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FriendRequestRepositoryImpl implements FriendRequestRepository {

    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.openSession();
    }

    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {
        try(Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            return Optional.of(entity);
        }
    }

    @Override
    public Optional<FriendRequest> findById(Long primaryKey) {
        try(Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            FriendRequest friendRequest = (FriendRequest) session.find(FriendRequest.class, primaryKey);
            transaction.commit();
            return Optional.ofNullable(friendRequest);
        }
    }

    @Override
    public Optional<List<FriendRequest>> findAll() {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long primaryKey) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            Optional<FriendRequest> request = findById(primaryKey);
            request.ifPresent(session::remove);
            transaction.commit();
        }
    }

    @Override
    public boolean existsById(Long primaryKey) {
        return false;
    }

    @Override
    public Optional<FriendRequest> findBySenderAndReceiver(String sender,String receiver) {
        return Optional.empty();
    }

    @Override
    public Optional<List<FriendRequest>> findBySender(Long userId) {
        try (Session session = getSession()) {
        String hql = "FROM FriendRequest r JOIN FETCH r.sender JOIN FETCH r.recipient WHERE r.sender.id = :userId ORDER BY r.createdAt DESC";
        Query<FriendRequest> query = session.createQuery(hql, FriendRequest.class)
                .setParameter("userId", userId);
        return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<List<FriendRequest>> findByReceiver(Long userId) {
        try (Session session = getSession()) {
            String hql = "FROM FriendRequest r JOIN FETCH r.sender JOIN FETCH r.recipient WHERE r.recipient.id = :userId ORDER BY r.createdAt DESC";
            Query<FriendRequest> query = session.createQuery(hql, FriendRequest.class)
                    .setParameter("userId", userId);
            return Optional.ofNullable(query.list());
        }
    }
}
