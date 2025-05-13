package ru.senla.javacourse.mutovin.messenger.impl.exception;

public class PostException extends RuntimeException {
    public PostException(String message) {
        super(message);
    }

    public PostException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class PostNotFoundException extends PostException {
        public PostNotFoundException(Long postId) {
            super("Пост не найдено с id: " + postId);
        }
    }

    public static class PostAccessDeniedException extends PostException {
        public PostAccessDeniedException(Long postId, Long userId) {
            super("Пользователь с id " + userId + " не имеет доступа к посту " + postId);
        }
    }

    public static class PostNotEditableException extends PostException {
        public PostNotEditableException(Long postId) {
            super("Пост с id " + postId + " нельзя редактировать");
        }
    }

    public static class PostNotDeletableException extends PostException {
        public PostNotDeletableException(Long postId) {
            super("Пост с id " + postId + " нельзя удалить");
        }
    }

    public static class EmptyPostContentException extends PostException {
        public EmptyPostContentException() {
            super("Содержимое поста не может быть пустым");
        }
    }

}
