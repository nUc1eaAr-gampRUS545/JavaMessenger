package ru.senla.javacourse.mutovin.messenger.impl.exception;

public class MessageException extends RuntimeException {
    public MessageException(String message) {
        super(message);
    }

    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class MessageNotFoundException extends MessageException {
        public MessageNotFoundException(Long messageId) {
            super("Сообщение не найдено с id: " + messageId);
        }
    }

    public static class MessageAccessDeniedException extends MessageException {
        public MessageAccessDeniedException(Long messageId, Long userId) {
            super("Пользователь с id " + userId + " не имеет доступа к сообщению " + messageId);
        }
    }

    public static class MessageNotEditableException extends MessageException {
        public MessageNotEditableException(Long messageId) {
            super("Сообщение с id " + messageId + " нельзя редактировать");
        }
    }

    public static class MessageNotDeletableException extends MessageException {
        public MessageNotDeletableException(Long messageId) {
            super("Сообщение с id " + messageId + " нельзя удалить");
        }
    }

    public static class EmptyMessageContentException extends MessageException {
        public EmptyMessageContentException() {
            super("Содержимое сообщения не может быть пустым");
        }
    }

    public static class UserNotInChatException extends MessageException {
        public UserNotInChatException(Long userId, Long chatId) {
            super("Пользователь с ID " + userId + " не является участником чата " + chatId);
        }
    }
} 