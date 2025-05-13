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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.openSession();
    }

    @Override
    public Optional<Message> save(Message message) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(message);
            transaction.commit();
        }
        return Optional.ofNullable(message);
    }

    @Override
    public Optional<Message> findById(Long id) {
        try (Session session = getSession()) {
            return Optional.ofNullable(session.find(Message.class, id));
        }
    }

    @Override
    public Optional<List<Message>> findAll() {
        try (Session session = getSession()) {
            return Optional.ofNullable(session.createQuery("from Message", Message.class).list());
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            Optional<Message> message = findById(id);
            message.ifPresent(session::remove);
            transaction.commit();
        }
    }

    @Override
    public boolean existsById(Long primaryKey) {
        try (Session session = getSession()) {
            Message entity = session.get(Message.class, primaryKey);
            return entity != null;
        }
    }

    @Override
    public Optional<List<Message>> findByChatId(Long chatId) {
        try (Session session = getSession()) {
            String hql = "FROM Message m JOIN FETCH m.sender JOIN FETCH m.chat WHERE m.chat.id = :chatId ORDER BY m.createdAt DESC";
            Query<Message> query = session.createQuery(hql, Message.class)
                    .setParameter("chatId", chatId);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<List<Message>> findBySenderId(Long senderId) {
        try (Session session = getSession()) {
            String hql = "FROM Message m JOIN FETCH m.sender JOIN FETCH m.chat WHERE m.sender.id = :senderId ORDER BY m.createdAt DESC";
            Query<Message> query = session.createQuery(hql, Message.class)
                    .setParameter("senderId", senderId);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<List<Message>> findByChatIdAndStatus(Long chatId, MessageStatus status) {
        try (Session session = getSession()) {
            String hql = "FROM Message m JOIN FETCH m.sender JOIN FETCH m.chat WHERE m.chat.id = :chatId AND m.status = :status ORDER BY m.createdAt DESC";
            Query<Message> query = session.createQuery(hql, Message.class)
                    .setParameter("chatId", chatId)
                    .setParameter("status", status);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<Message> update(Message message) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(message);
            transaction.commit();
        }
        return Optional.ofNullable(message);
    }

    @Override
    public Optional<List<Message>> findUnreadMessagesByUserId(Long userId) {
        try (Session session = getSession()) {
            String hql = "FROM Message m JOIN FETCH m.sender JOIN FETCH m.chat WHERE m.chat.id IN " +
                    "(SELECT cp.chat.id FROM ChatParticipant cp WHERE cp.user.id = :userId) " +
                    "AND m.status = :status AND m.sender.id != :userId ORDER BY m.createdAt DESC";
            Query<Message> query = session.createQuery(hql, Message.class)
                    .setParameter("userId", userId)
                    .setParameter("status", MessageStatus.SENT);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<List<Message>> findMessagesByChatIdAndUserId(Long chatId, Long userId) {
        try (Session session = getSession()) {
            String hql = "FROM Message m JOIN FETCH m.sender JOIN FETCH m.chat WHERE m.chat.id = :chatId " +
                    "AND m.chat.id IN (SELECT cp.chat.id FROM ChatParticipant cp WHERE cp.user.id = :userId) " +
                    "ORDER BY m.createdAt DESC";
            Query<Message> query = session.createQuery(hql, Message.class)
                    .setParameter("chatId", chatId)
                    .setParameter("userId", userId);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<Set<Message>> findAllByMessageIds(Set<Long> messageIds) {
        Set<Message> messages = new HashSet<>();
        messageIds.forEach(messageId -> {
            Optional<Message> message = findById(messageId);
            message.ifPresent(messages::add);
        });
        return Optional.of(messages);
    }
} 