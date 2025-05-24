# Task Manager

Приложение для управления задачами, построенное на Spring Boot.

## Технологии

- Java 17
- Spring Boot 3.2.3
- PostgreSQL 15
- Flyway для миграций базы данных
- Docker и Docker Compose
- JUnit 5 и TestContainers для тестирования

## Требования

- JDK 17 или выше
- Docker и Docker Compose
- Gradle 8.x

## Запуск приложения

### Локальный запуск

1. Запустите PostgreSQL:
```bash
docker compose up -d db
```

2. Запустите приложение:
```bash
./gradlew bootRun --args='--spring.profiles.active=postgres'
```

### Запуск в Docker

```bash
docker compose up --build
```

## Тестирование

### Запуск тестов

```bash
./gradlew test
```

### Интеграционные тесты

Интеграционные тесты используют TestContainers для создания изолированной PostgreSQL базы данных.

## Структура проекта

- `src/main/java` - исходный код приложения
- `src/main/resources` - конфигурационные файлы
- `src/main/resources/db/migration` - миграции базы данных
- `src/test` - тесты

## Профили

- `postgres` - основной профиль с PostgreSQL
- `inmemory` - профиль с хранением в памяти (для тестирования)

## API Endpoints

- `POST /users` - регистрация нового пользователя
- `GET /users/{username}` - получение информации о пользователе
- `POST /tasks` - создание новой задачи
- `GET /tasks` - получение списка задач
- `PUT /tasks/{id}` - обновление задачи
- `DELETE /tasks/{id}` - удаление задачи

## База данных

Приложение использует PostgreSQL в качестве основной базы данных. Схема базы данных управляется с помощью Flyway миграций.

### Таблицы

- `users` - пользователи
- `tasks` - задачи
- `notifications` - уведомления

## Лицензия

MIT 