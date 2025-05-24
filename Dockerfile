# Используем JDK 17 как базовый образ
FROM eclipse-temurin:17-jdk-alpine

# Создаем рабочую директорию
WORKDIR /app

# Копируем файлы сборки
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Даем права на выполнение gradlew
RUN chmod +x ./gradlew

# Собираем приложение
RUN ./gradlew build -x test

# Создаем слой для запуска
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Копируем только собранный JAR файл
COPY --from=0 /app/build/libs/*.jar app.jar

# Открываем порт
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"] 