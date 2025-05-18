package ru.senla.javacourse.mutovin.messenger.impl.exception;

public class ChatException extends RuntimeException {
    public ChatException(String message) {
        super(message);
    }

    public ChatException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class ChatNotFoundException extends ChatException {
        public ChatNotFoundException(Long chatId) {
            super("Чат не найден с id: " + chatId);
        }
    }

    public static class ChatNameAlreadyExistsException extends ChatException {
        public ChatNameAlreadyExistsException(String name) {
            super("Чат с именем '" + name + "' уже существует");
        }
    }

    public static class PrivateChatAlreadyExistsException extends ChatException {
        public PrivateChatAlreadyExistsException(Long userId1, Long userId2) {
            super("Приватный чат между пользователями " + userId1 + " и " + userId2 + " уже существует");
        }
    }

    public static class UserNotInChatException extends ChatException {
        public UserNotInChatException(Long userId, Long chatId) {
            super("Пользователь " + userId + " не является участником чата " + chatId);
        }
    }

    public static class UserNotAdminException extends ChatException {
        public UserNotAdminException(Long userId, Long chatId) {
            super("Пользователь " + userId + " не является администратором чата " + chatId);
        }
    }
    public static class UserAccessDeniedException extends ChatException {
        public UserAccessDeniedException(Long chatId, Long userId) {
            super("Пользователь с id " + userId + " не имеет доступа к чату " + chatId);
        }
    }
} 