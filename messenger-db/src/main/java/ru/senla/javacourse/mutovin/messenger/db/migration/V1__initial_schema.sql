-- Создание таблицы ролей
CREATE TABLE roles (
    name VARCHAR(50) PRIMARY KEY
);

-- Создание таблицы статусов сообщений
CREATE TABLE message_statuses (
    name VARCHAR(50) PRIMARY KEY
);

-- Создание таблицы статусов дружбы
CREATE TABLE friend_request_statuses (
    name VARCHAR(50) PRIMARY KEY
);

-- Создание таблицы статусов постов
CREATE TABLE post_statuses (
    name VARCHAR(50) PRIMARY KEY
);

-- Создание таблицы пользователей
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(15) NOT NULL,
    firstname VARCHAR(25) NOT NULL,
    lastname VARCHAR(25) NOT NULL,
    age INTEGER NOT NULL,
    gender VARCHAR(255) NOT NULL,
    email VARCHAR(35) NOT NULL,
    phone_number VARCHAR(18) NOT NULL,
    password TEXT NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_username (username),
    UNIQUE KEY unique_email (email),
    UNIQUE KEY unique_phone_number (phone_number),
    FOREIGN KEY (role_name) REFERENCES roles(name)
);

-- Создание таблицы сообществ
CREATE TABLE communities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Создание таблицы чатов
CREATE TABLE chats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    is_private BOOLEAN NOT NULL,
    creator_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES users(id)
);

-- Создание таблицы участников чата
CREATE TABLE chat_participants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    chat_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    left_at TIMESTAMP,
    is_admin BOOLEAN NOT NULL,
    FOREIGN KEY (chat_id) REFERENCES chats(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Создание таблицы сообщений
CREATE TABLE messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    chat_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    read_at TIMESTAMP,
    status_name VARCHAR(50) NOT NULL,
    FOREIGN KEY (chat_id) REFERENCES chats(id),
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (status_name) REFERENCES message_statuses(name)
);

-- Создание таблицы заявок в друзья
CREATE TABLE friend_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    status_name VARCHAR(50) NOT NULL,
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (status_name) REFERENCES friend_request_statuses(name),
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (recipient_id) REFERENCES users(id)
);

-- Создание таблицы дружбы
CREATE TABLE friendships (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (friend_id) REFERENCES users(id)
);

-- Создание таблицы постов
CREATE TABLE posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    creator_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    read_at TIMESTAMP,
    status_name VARCHAR(50) NOT NULL,
    FOREIGN KEY (creator_id) REFERENCES users(id),
    FOREIGN KEY (status_name) REFERENCES post_statuses(name)
);

-- Создание таблицы связи постов с сообществами
CREATE TABLE post_community (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    community_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (community_id) REFERENCES communities(id),
    FOREIGN KEY (post_id) REFERENCES posts(id)
);

-- Создание таблицы подписок
CREATE TABLE subscribes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    community_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (community_id) REFERENCES communities(id),
    FOREIGN KEY (member_id) REFERENCES users(id)
);

-- Добавление начальных данных для перечислений
INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');
INSERT INTO message_statuses (name) VALUES ('SENT'), ('DELIVERED'), ('READ');
INSERT INTO friend_request_statuses (name) VALUES ('PENDING'), ('ACCEPTED'), ('REJECTED');
INSERT INTO post_statuses (name) VALUES ('DRAFT'), ('PUBLISHED'), ('ARCHIVED');
