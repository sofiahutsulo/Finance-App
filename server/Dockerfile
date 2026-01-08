# Multi-stage build для Ktor FinanceManager Server
# Практикум №5: Інтеграція та розгортання системи

# Stage 1: Builder - сборка приложения
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Копируем Gradle wrapper
COPY ../gradle ./gradle
COPY ../gradlew ./
COPY ../gradlew.bat ./

# Копируем файлы Gradle для кеширования зависимостей
COPY ../settings.gradle.kts ./
COPY ../build.gradle.kts ./
COPY ../gradle.properties ./
COPY build.gradle.kts ./server/

# Копируем исходный код
COPY src ./server/src

# Собираем приложение
RUN ./gradlew :server:installDist --no-daemon

# Stage 2: Runtime - запуск приложения
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Создаем пользователя для безопасности
RUN addgroup -S finance && adduser -S finance -G finance

# Копируем собранное приложение из builder stage
COPY --from=builder /app/server/build/install/server /app

# Меняем владельца файлов
RUN chown -R finance:finance /app

# Переключаемся на непривилегированного пользователя
USER finance

# Открываем порт
EXPOSE 8080

# Переменные окружения (можно переопределить при запуске)
ENV DB_HOST=localhost \
    DB_PORT=5432 \
    DB_NAME=finance_manager \
    DB_USER=postgres \
    DB_PASSWORD=postgres

# Запуск сервера
CMD ["/app/bin/server"]
