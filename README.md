# JavaMessenger

## Описание

JavaMessenger - это современное веб-приложение для обмена сообщениями, построенное с использованием Spring Boot и Java. [Ссылка на GitHub](https://github.com/nUc1eaAr-gampRUS545/JavaMessenger.git) - это современное веб-приложение для обмена сообщениями, построенное с использованием Spring Boot и Java.

### Запуск приложения и остального окружения

1. Запуск контейнеров:

Запускает контейнеры с Redis, PosgreSQL, Kafka
Для запуска контейнеров выполните следующие шаги:

```bash
docker-compose up 
```

2. Для сборки приложения оповещения:

```bash
docker build -t app .
```

3. Запуск непосредственно Java Messenger

Для локального запуска приложения вам потребуется:

- Java 17+
- Maven
- PostgreSQL (если используется база данных)

1) Сборка проекта:

```bash
mvn clean install -DskipTests
```

2) Запуск приложения:

```bash
mvn spring-boot:run -pl messenger-application
```

Приложение будет доступно по адресу: http://localhost:8080


## API документация

После запуска приложения документация API будет доступна по адресу:
http://localhost:8080/swagger-ui/index.html

## Технологии

- Spring Boot
- Spring Security
- JWT для аутентификации
- Kafka
- Redis
- Flyway
- PostgreSQL
- Maven
- Docker
- Swagger/OpenAPI для документации API

## Настройки

Основные настройки приложения находятся в файле `application.yaml` в модуле `messenger-impl`.

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