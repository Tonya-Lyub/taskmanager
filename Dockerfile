# Использование образа JDK
FROM openjdk:17-jdk-slim

# Установка рабочей директории
WORKDIR /app

# Копирование артефакта (например, .jar) в контейнер
COPY build/libs/taskmanager-0.0.1-SNAPSHOT.jar app.jar

# Открытие порта
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
