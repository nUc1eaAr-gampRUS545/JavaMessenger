package ru.senla.javacourse.mutovin.messenger.impl.repository.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.senla.javacourse.mutovin.messenger.db.entity.Chat;
import ru.senla.javacourse.mutovin.messenger.db.entity.Community;
import ru.senla.javacourse.mutovin.messenger.impl.repository.ChatRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ChatRepositoryImpl implements ChatRepository {

    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.openSession();
    }

    @Override
    public Optional<Chat> save(Chat chat) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            Chat mergedChat = (Chat) session.merge(chat);
            transaction.commit();
            return Optional.ofNullable(mergedChat);
        }

    }

    @Override
    public Optional<Chat> findById(Long primaryKey) {
        try (Session session = getSession()) {
            Chat chat = session.get(Chat.class,primaryKey);
            return Optional.of(chat);
        }

    }

    @Override
    public Optional<Chat> findChatById(Long id,Long userId) {
        try (Session session = getSession()) {
            String hql = "FROM Chat c JOIN FETCH c.participants p WHERE c.id = :id AND p.id = :userId";
            ;
            Query<Chat> query = session.createQuery(hql,Chat.class)
                    .setParameter("userId",userId)
                    .setParameter("id",id);
            return Optional.ofNullable((Chat) query);

        }
    }

    @Override
    public Optional<List<Chat>> findAll() {
        try (Session session = getSession()) {
            return Optional.ofNullable(session.createQuery("from Chat",Chat.class).list());
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();

            session.createNativeQuery("DELETE FROM messages WHERE chat_id = :chatId")
                    .setParameter("chatId",id)
                    .executeUpdate();

            session.createNativeQuery("DELETE FROM chat_participants WHERE chat_id = :chatId")
                    .setParameter("chatId",id)
                    .executeUpdate();

            session.createNativeQuery("DELETE FROM chats WHERE id = :chatId")
                    .setParameter("chatId",id)
                    .executeUpdate();

            transaction.commit();
        }
    }

    @Override
    public boolean existsById(Long primaryKey) {
        try (Session session = getSession()) {
            Chat entity = session.get(Chat.class,primaryKey);
            return entity!=null;
        }
    }

    @Override
    public Optional<List<Chat>> findByCreatorId(Long creatorId) {
        try (Session session = getSession()) {
            String hql = "FROM Chat c WHERE c.creator.id = :creatorId ORDER BY c.createdAt DESC";
            Query<Chat> query = session.createQuery(hql,Chat.class)
                    .setParameter("creatorId",creatorId);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<List<Chat>> findByParticipantId(Long participantId) {
        try (Session session = getSession()) {
            String hql = "SELECT c FROM Chat c JOIN c.participants p WHERE p.user.id = :participantId " +
                    "AND p.leftAt IS NULL ORDER BY c.createdAt DESC";
            Query<Chat> query = session.createQuery(hql,Chat.class)
                    .setParameter("participantId",participantId);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<Chat> findPrivateChatByParticipants(Long userId1,Long userId2) {
        try (Session session = getSession()) {
            String hql = "SELECT c FROM Chat c WHERE c.isPrivate = true " +
                    "AND c.id IN (SELECT cp1.chat.id FROM ChatParticipant cp1 WHERE cp1.user.id = :userId1) " +
                    "AND c.id IN (SELECT cp2.chat.id FROM ChatParticipant cp2 WHERE cp2.user.id = :userId2) " +
                    "AND (SELECT COUNT(cp) FROM ChatParticipant cp WHERE cp.chat.id = c.id AND cp.leftAt IS NULL) = 2";
            Query<Chat> query = session.createQuery(hql,Chat.class)
                    .setParameter("userId1",userId1)
                    .setParameter("userId2",userId2);
            return query.uniqueResultOptional();
        }
    }

    @Override
    public Optional<Chat> update(Chat chat) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(chat);
            transaction.commit();
        }
        return Optional.ofNullable(chat);
    }

    @Override
    public Optional<List<Chat>> findActiveChatsByUserId(Long userId) {
        try (Session session = getSession()) {
            String hql = "SELECT c FROM Chat c JOIN c.participants p " +
                    "WHERE p.user.id = :userId AND p.leftAt IS NULL " +
                    "ORDER BY c.updatedAt DESC";
            Query<Chat> query = session.createQuery(hql,Chat.class)
                    .setParameter("userId",userId);
            return Optional.ofNullable(query.list());
        }
    }

    @Override
    public Optional<Set<Chat>> findAllByChatIds(List<Long> chatIds) {
        Set<Chat> chats = new HashSet<>();
        chatIds.forEach(chatId -> {
            Optional<Chat> chat = findById(chatId);
            chat.ifPresent(chats::add);
        });
        return Optional.of(chats);
    }

    @Override
    public boolean existsByName(String name) {
        try (Session session = getSession()) {
            String hql = "SELECT count(c) FROM Chat c WHERE c.name = :name";
            Query<Long> query = session.createQuery(hql,Long.class)
                    .setParameter("name",name);
            return query.uniqueResult() > 0;
        }
    }
} 