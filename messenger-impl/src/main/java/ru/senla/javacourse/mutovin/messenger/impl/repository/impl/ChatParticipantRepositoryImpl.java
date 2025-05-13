package ru.senla.javacourse.mutovin.messenger.impl.repository.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.senla.javacourse.mutovin.messenger.db.entity.ChatParticipant;
import ru.senla.javacourse.mutovin.messenger.impl.repository.ChatParticipantRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ChatParticipantRepositoryImpl implements ChatParticipantRepository {

    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.openSession();
    }

    @Override
    public Optional<ChatParticipant> save(ChatParticipant participant) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(participant);
            transaction.commit();
        }
        return Optional.ofNullable(participant);
    }

    @Override
    public Optional<ChatParticipant> findById(Long id) {
        try (Session session = getSession()) {
            return Optional.ofNullable(session.find(ChatParticipant.class, id));
        }
    }

    @Override
    public Optional<List<ChatParticipant>> findAll() {
        try (Session session = getSession()) {
            return Optional.ofNullable(session.createQuery("from ChatParticipant", ChatParticipant.class).list());
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            Optional<ChatParticipant> participant = findById(id);
            participant.ifPresent(session::remove);
            transaction.commit();
        }
    }

    @Override
    public boolean existsById(Long primaryKey) {
        try (Session session = getSession()) {
            ChatParticipant entity = session.get(ChatParticipant.class, primaryKey);
            return entity != null;
        }
    }

    @Override
    public Optional<List<ChatParticipant>> findByChatId(Long chatId) {
        try (Session session = getSession()) {
            String hql = "FROM ChatParticipant cp WHERE cp.chat.id = :chatId AND cp.leftAt IS NULL";
            Query<ChatParticipant> query = session.createQuery(hql, ChatParticipant.class)
                    .setParameter("chatId", chatId);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<List<ChatParticipant>> findByUserId(Long userId) {
        try (Session session = getSession()) {
            String hql = "FROM ChatParticipant cp WHERE cp.user.id = :userId AND cp.leftAt IS NULL";
            Query<ChatParticipant> query = session.createQuery(hql, ChatParticipant.class)
                    .setParameter("userId", userId);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<ChatParticipant> findByChatIdAndUserId(Long chatId, Long userId) {
        try (Session session = getSession()) {
            String hql = "FROM ChatParticipant cp WHERE cp.chat.id = :chatId AND cp.user.id = :userId AND cp.leftAt IS NULL";
            Query<ChatParticipant> query = session.createQuery(hql, ChatParticipant.class)
                    .setParameter("chatId", chatId)
                    .setParameter("userId", userId);
            return query.uniqueResultOptional();
        }
    }

    @Override
    public Optional<ChatParticipant> update(ChatParticipant participant) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(participant);
            transaction.commit();
        }
        return Optional.ofNullable(participant);
    }

    @Override
    public Optional<List<ChatParticipant>> findActiveParticipantsByChatId(Long chatId) {
        try (Session session = getSession()) {
            String hql = "FROM ChatParticipant cp WHERE cp.chat.id = :chatId AND cp.leftAt IS NULL";
            Query<ChatParticipant> query = session.createQuery(hql, ChatParticipant.class)
                    .setParameter("chatId", chatId);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<Set<ChatParticipant>> findAllByParticipantIds(Set<Long> participantIds) {
        Set<ChatParticipant> participants = new HashSet<>();
        participantIds.forEach(participantId -> {
            Optional<ChatParticipant> participant = findById(participantId);
            participant.ifPresent(participants::add);
        });
        return Optional.of(participants);
    }

    @Override
    public boolean isUserAdminInChat(Long userId, Long chatId) {
        try (Session session = getSession()) {
            String hql = "SELECT count(cp) FROM ChatParticipant cp " +
                    "WHERE cp.user.id = :userId AND cp.chat.id = :chatId AND cp.isAdmin = true AND cp.leftAt IS NULL";
            Query<Long> query = session.createQuery(hql, Long.class)
                    .setParameter("userId", userId)
                    .setParameter("chatId", chatId);
            return query.uniqueResult() > 0;
        }
    }
} 