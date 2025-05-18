package ru.senla.javacourse.mutovin.messenger.impl.repository.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.senla.javacourse.mutovin.messenger.db.entity.Message;
import ru.senla.javacourse.mutovin.messenger.db.entity.MessageStatus;
import ru.senla.javacourse.mutovin.messenger.impl.repository.MessageRepository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Optional<Message> save(Message message) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        Message result = session.merge(message);
        transaction.commit();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Message> findById(Long id) {
        Session session = getSession();
        return Optional.ofNullable(session.find(Message.class, id));
    }

    @Override
    public Optional<List<Message>> findAll() {
        Session session = getSession();
        return Optional.ofNullable(session.createQuery("from Message", Message.class).list());
    }

    @Override
    public void deleteById(Long id) {
        Session session = getSession();
        Message message = session.find(Message.class, id);
        if (message != null) {
            session.remove(message);
        }
    }

    @Override
    public boolean existsById(Long primaryKey) {
        Session session = getSession();
        return session.get(Message.class, primaryKey) != null;
    }

    @Override
    public Optional<List<Message>> findByChatId(Long chatId) {
        Session session = getSession();
        String hql = "FROM Message m JOIN FETCH m.sender JOIN FETCH m.chat WHERE m.chat.id = :chatId ORDER BY m.createdAt DESC";
        Query<Message> query = session.createQuery(hql, Message.class)
                .setParameter("chatId", chatId);
        return Optional.ofNullable(query.list());
    }

    @Override
    public Optional<List<Message>> findBySenderId(Long senderId) {
        Session session = getSession();
        String hql = "FROM Message m JOIN FETCH m.sender JOIN FETCH m.chat WHERE m.sender.id = :senderId ORDER BY m.createdAt DESC";
        Query<Message> query = session.createQuery(hql, Message.class)
                .setParameter("senderId", senderId);
        return Optional.ofNullable(query.list());
    }

    @Override
    public Optional<List<Message>> findByChatIdAndStatus(Long chatId, MessageStatus status) {
        Session session = getSession();
        String hql = "FROM Message m JOIN FETCH m.sender JOIN FETCH m.chat WHERE m.chat.id = :chatId AND m.status = :status ORDER BY m.createdAt DESC";
        Query<Message> query = session.createQuery(hql, Message.class)
                .setParameter("chatId", chatId)
                .setParameter("status", status);
        return Optional.ofNullable(query.list());
    }

    @Override
    public Optional<Message> update(Message message) {
        Session session = getSession();
        Message updated = session.merge(message);
        return Optional.ofNullable(updated);
    }

    @Override
    public Optional<List<Message>> findUnreadMessagesByUserId(Long userId) {
        Session session = getSession();
        String hql = "FROM Message m JOIN FETCH m.sender JOIN FETCH m.chat WHERE m.chat.id IN " +
                "(SELECT cp.chat.id FROM ChatParticipant cp WHERE cp.user.id = :userId) " +
                "AND m.status = :status AND m.sender.id != :userId ORDER BY m.createdAt DESC";
        Query<Message> query = session.createQuery(hql, Message.class)
                .setParameter("userId", userId)
                .setParameter("status", MessageStatus.SENT);
        return Optional.ofNullable(query.list());
    }

    @Override
    public Optional<List<Message>> findMessagesByChatIdAndUserId(Long chatId, Long userId) {
        Session session = getSession();
        String hql = "FROM Message m JOIN FETCH m.sender JOIN FETCH m.chat WHERE m.chat.id = :chatId " +
                "AND m.chat.id IN (SELECT cp.chat.id FROM ChatParticipant cp WHERE cp.user.id = :userId) " +
                "ORDER BY m.createdAt DESC";
        Query<Message> query = session.createQuery(hql, Message.class)
                .setParameter("chatId", chatId)
                .setParameter("userId", userId);
        return Optional.ofNullable(query.list());
    }

    @Override
    public Optional<Set<Message>> findAllByMessageIds(Set<Long> messageIds) {
        Session session = getSession();
        Set<Message> messages = new HashSet<>();
        for (Long id : messageIds) {
            Message msg = session.find(Message.class, id);
            if (msg != null) messages.add(msg);
        }
        return Optional.of(messages);
    }
}
