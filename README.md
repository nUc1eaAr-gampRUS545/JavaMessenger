# JavaMessenger

## Описание
JavaMessenger - это современное веб-приложение для обмена сообщениями, построенное с использованием Spring Boot и Java.

## Способы запуска

### 1. Запуск через Docker

Для запуска приложения через Docker выполните следующие шаги:

1. Запуск контейнеров:
```bash
docker-compose up
```
2. Для сборки приложения оповещения:
```bash
docker build -t app . 
```
### 2. Запуск локально

Для локального запуска приложения вам потребуется:
- Java 17+
- Maven
- PostgreSQL (если используется база данных)

1. Сборка проекта:
```bash
mvn clean install
```

2. Запуск приложения:
```bash
mvn spring-boot:run -pl messenger-application
```

Приложение будет доступно по адресу: http://localhost:8080

### 3. Запуск в режиме разработки

Для запуска в режиме разработки используйте:
```bash
mvn spring-boot:run
```

## API документация

После запуска приложения документация API будет доступна по адресу:
http://localhost:8080/swagger-ui/index.html

## Технологии
- Spring Boot
- Spring Security
- JWT для аутентификации
- Kafka
- Redis
- Flayway
- PostgreSQL
- Maven
- Docker
- Swagger/OpenAPI для документации API

## Настройки

Основные настройки приложения находятся в файле `application.properties` в модуле `messenger-application`.

## Структура проекта

Проект состоит из следующих модулей:
- `messenger-api` - интерфейсы и модели
- `messenger-impl` - реализация сервисов
- `messenger-db` - конфигурация базы данных
- `messenger-application` - основное приложение

## Безопасность

Приложение использует JWT токены для аутентификации. Все конечные точки API защищены и требуют валидный токен доступа.

## Тестирование

Для запуска тестов используйте:
```bash
mvn test
```