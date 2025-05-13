-- Создание таблицы друзей
CREATE TABLE friendships (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_friend FOREIGN KEY (friend_id) REFERENCES users(id),
    CONSTRAINT unique_friendship UNIQUE (user_id, friend_id)
);

-- Создание индексов для таблицы друзей
CREATE INDEX idx_friendships_user_id ON friendships(user_id);
CREATE INDEX idx_friendships_friend_id ON friendships(friend_id);

-- Создание таблицы запросов в друзья
CREATE TABLE friend_requests (
    id SERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES users(id),
    CONSTRAINT fk_recipient FOREIGN KEY (recipient_id) REFERENCES users(id),
    CONSTRAINT unique_friend_request UNIQUE (sender_id, recipient_id)
);

-- Создание индексов для таблицы запросов в друзья
CREATE INDEX idx_friend_requests_sender_id ON friend_requests(sender_id);
CREATE INDEX idx_friend_requests_recipient_id ON friend_requests(recipient_id); 