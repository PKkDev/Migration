package com.sandbox.core;

/// Pattern Matching & Switch Expressions
public class PatternMatchingDemo {
    // Иерархия ответов (sealed interface гарантирует, что других реализаций быть не может)
    public sealed interface ApiResponse permits Success, ClientError, ServerError {
    }

    public record Success(String body, int statusCode) implements ApiResponse {
    }

    public record ClientError(String message, int errorCode) implements ApiResponse {
    }

    public record ServerError(Throwable exception) implements ApiResponse {
    }

    static void main(String[] args) {
        ApiResponse response = new ClientError("Пользователь не найден", 404);

        // Switch expression с деконструкцией рекорда и фильтром 'when'
        String resultDescription = switch (response) {
            case Success(String body, int code) -> "Успех [" + code + "]: " + body;

            // Деструктурируем поля и проверяем guard condition через 'when'
            case ClientError(String msg, int code) when code == 404 -> "Ресурс не найден (404): " + msg;

            case ClientError(String msg, int code) -> "Ошибка клиента (" + code + "): " + msg;

            case ServerError(Throwable ex) -> "Фатальная ошибка: " + ex.getMessage();

            // default не нужен, так как sealed interface покрывает ВСЕ возможные варианты
        };

        System.out.println(resultDescription);

        // Pattern matching для instanceof
        Object rawObject = "Привет, мир";
        if (rawObject instanceof String text && !text.isEmpty()) {
            System.out.println("Строка в верхнем регистре: " + text.toUpperCase());
        }
    }
}
