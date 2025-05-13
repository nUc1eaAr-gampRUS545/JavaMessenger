-- Создание таблицы пользователей
CREATE TABLE users
(
    id           SERIAL PRIMARY KEY,
    username     VARCHAR(15) UNIQUE NOT NULL,
    firstname    VARCHAR(25)        NOT NULL,
    lastname     VARCHAR(25)        NOT NULL,
    email        VARCHAR(35) UNIQUE NOT NULL,
    phone_number VARCHAR(18)        NOT NULL,
    password     TEXT               NOT NULL,
    role         VARCHAR(20)        NOT NULL CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN'))
);

-- Создание таблицы чатов
CREATE TABLE chats
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    is_private BOOLEAN      NOT NULL,
    creator_id BIGINT       NOT NULL REFERENCES users (id),
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_creator FOREIGN KEY (creator_id) REFERENCES users (id)
);

-- Создание таблицы участников чата
CREATE TABLE chat_participants
(
    id        SERIAL PRIMARY KEY,
    chat_id   BIGINT    NOT NULL,
    user_id   BIGINT    NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    left_at   TIMESTAMP,
    is_admin  BOOLEAN   NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_chat FOREIGN KEY (chat_id) REFERENCES chats (id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- Создание таблицы сообщений
CREATE TABLE messages
(
    id         SERIAL PRIMARY KEY,
    chat_id    BIGINT      NOT NULL,
    sender_id  BIGINT      NOT NULL,
    content    TEXT        NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    updated_at TIMESTAMP,
    read_at    TIMESTAMP,
    status     VARCHAR(20) NOT NULL CHECK (status IN ('SENT', 'DELIVERED', 'READ', 'EDITED', 'DELETED')),
    CONSTRAINT fk_chat_message FOREIGN KEY (chat_id) REFERENCES chats (id),
    CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES users (id)
);

-- Создание таблицы постов
CREATE TABLE posts
(
    id         SERIAL PRIMARY KEY,
    author_id  BIGINT    NOT NULL,
    content    TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    is_public  BOOLEAN   NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_post_author FOREIGN KEY (author_id) REFERENCES users (id)
);

-- Создание таблицы запросов в друзья
CREATE TABLE friend_requests
(
    id           SERIAL PRIMARY KEY,
    sender_id    BIGINT      NOT NULL,
    recipient_id BIGINT      NOT NULL,
    status       VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED')),
    created_at   TIMESTAMP   NOT NULL,
    updated_at   TIMESTAMP,
    CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES users (id),
    CONSTRAINT fk_receiver FOREIGN KEY (recipient_id) REFERENCES users (id),
    CONSTRAINT unique_friend_request UNIQUE (sender_id, recipient_id)
);

-- Создание таблицы друзей
CREATE TABLE friendships
(
    id         SERIAL PRIMARY KEY,
    user_id    BIGINT    NOT NULL,
    friend_id  BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_friend FOREIGN KEY (friend_id) REFERENCES users (id),
    CONSTRAINT unique_friendship UNIQUE (user_id, friend_id)
);

-- Создание индексов для оптимизации запросов
CREATE INDEX idx_chat_participants_chat_id ON chat_participants (chat_id);
CREATE INDEX idx_chat_participants_user_id ON chat_participants (user_id);

CREATE INDEX idx_messages_chat_id ON messages (chat_id);
CREATE INDEX idx_messages_sender_id ON messages (sender_id);
CREATE INDEX idx_messages_created_at ON messages (created_at);

CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);

CREATE INDEX idx_friendships_user_id ON friendships(user_id);
CREATE INDEX idx_friendships_friend_id ON friendships(friend_id);

CREATE INDEX idx_posts_author_id ON posts (author_id);
CREATE INDEX idx_posts_created_at ON posts (created_at);

CREATE INDEX idx_friend_requests_sender_id ON friend_requests (sender_id);
CREATE INDEX idx_friend_requests_receiver_id ON friend_requests (recipient_id);
CREATE INDEX idx_friend_requests_status ON friend_requests (status);