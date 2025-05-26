# Task Manager

Система управления задачами с поддержкой уведомлений и кэширования.

## Технологии

- Java 17
- Spring Boot
- PostgreSQL
- Redis
- RabbitMQ
- Docker
- Gradle

## Требования

- Docker и Docker Compose
- JDK 17
- Gradle

## Запуск

1. Клонируйте репозиторий:
```bash
git clone https://github.com/yourusername/taskmanager.git
```

2. Запустите приложение с помощью Docker Compose:
```bash
docker-compose up --build
```

## API Endpoints

### Задачи
- `POST /api/tasks` - Создание задачи
- `GET /api/tasks` - Получение списка задач
- `GET /api/tasks/{id}` - Получение задачи по ID
- `PUT /api/tasks/{id}` - Обновление задачи
- `DELETE /api/tasks/{id}` - Удаление задачи

### Пользователи
- `POST /api/users` - Создание пользователя
- `GET /api/users` - Получение списка пользователей
- `GET /api/users/{id}` - Получение пользователя по ID

### Уведомления
- `GET /api/notifications` - Получение списка уведомлений
- `GET /api/notifications/{id}` - Получение уведомления по ID

## Тестирование

Запуск тестов:
```bash
./gradlew test
```

## Профили

- `rabbit` - Активация RabbitMQ
- `redis` - Активация Redis кэширования
- `postgres` - Использование PostgreSQL

## Структура проекта

- `src/main/java` - исходный код приложения
- `src/main/resources` - конфигурационные файлы
- `src/main/resources/db/migration` - миграции базы данных
- `src/test` - тесты

## База данных

Приложение использует PostgreSQL в качестве основной базы данных. Схема базы данных управляется с помощью Flyway миграций.

### Таблицы

- `users` - пользователи
- `tasks` - задачи
- `notifications` - уведомления

## Лицензия

MIT 