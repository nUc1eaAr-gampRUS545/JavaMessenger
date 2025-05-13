package ru.senla.javacourse.mutovin.messenger.impl.exception;

public class ChatParticipantException extends RuntimeException {

    public ChatParticipantException(String message) {
        super(message);
    }

    public ChatParticipantException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class ChatParticipantNotFoundException extends ChatParticipantException {
        public ChatParticipantNotFoundException(Long participantId) {
            super("Участник чата не найден с id: " + participantId);
        }
    }

    public static class UserAlreadyParticipantException extends ChatParticipantException {
        public UserAlreadyParticipantException(Long userId, Long chatId) {
            super("Пользователь " + userId + " уже является участником чата " + chatId);
        }
    }

    public static class ParticipantNotActiveException extends ChatParticipantException {
        public ParticipantNotActiveException(Long participantId) {
            super("Участник чата " + participantId + " неактивен (покинул чат)");
        }
    }

    public static class ParticipantAccessDeniedException extends ChatParticipantException {
        public ParticipantAccessDeniedException(Long participantId, String action) {
            super("Участник чата " + participantId + " не имеет прав для выполнения действия: " + action);
        }
    }


}
