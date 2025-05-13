package ru.senla.javacourse.mutovin.messenger.impl.exception;

public class FriendRequestException extends RuntimeException {

    public FriendRequestException(String message) {
        super(message);
    }

    public FriendRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class FriendRequestNotFoundException extends FriendRequestException {
        public FriendRequestNotFoundException(Long requestId) {
            super("Запрос в друзья с ID " + requestId + " не найден");
        }
    }

    public static class FriendRequestAlreadyExistsException extends FriendRequestException {
        public FriendRequestAlreadyExistsException(Long senderId, Long receiverId) {
            super("Запрос в друзья от пользователя " + senderId + " к пользователю " + receiverId + " уже существует");
        }
    }

    public static class FriendRequestAccessDeniedException extends FriendRequestException {
        public FriendRequestAccessDeniedException(Long requestId, Long userId) {
            super("Пользователь с ID " + userId + " не имеет доступа к запросу " + requestId);
        }
    }

    public static class CannotAcceptOwnRequestException extends FriendRequestException {
        public CannotAcceptOwnRequestException(Long requestId) {
            super("Нельзя принять собственный запрос в друзья (ID: " + requestId + ")");
        }
    }

    public static class CannotSendToSelfException extends FriendRequestException {
        public CannotSendToSelfException() {
            super("Нельзя отправить запрос в друзья самому себе");
        }
    }

    public static class AlreadyFriendsException extends FriendRequestException {
        public AlreadyFriendsException(Long userId1, Long userId2) {
            super("Пользователи " + userId1 + " и " + userId2 + " уже являются друзьями");
        }
    }

    public static class FriendRequestNotPendingException extends FriendRequestException {
        public FriendRequestNotPendingException(Long requestId) {
            super("Запрос с ID " + requestId + " уже был обработан (принят/отклонен)");
        }
    }

    public static class FriendRequestValidationException extends FriendRequestException {
        public FriendRequestValidationException(String message) {
            super("Ошибка валидации запроса в друзья: " + message);
        }
    }
}
