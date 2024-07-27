# Weather Project

## Описание

Этот проект представляет собой микросервисную архитектуру для работы с погодой и взаимодействия с Telegram ботом. Система позволяет пользователям получать информацию о погоде в их городах и получать уведомления о прогнозе погоды в их временной зоне. Проект состоит из нескольких микросервисов, включая сервис погоды, сервис пользователей и Telegram бот.

## Технологии

- **Java**: Основной язык программирования.
- **Spring Boot**: Фреймворк для создания микросервисов.
- **Hibernate**: ORM для работы с базой данных.
- **RabbitMQ**: Message broker для асинхронного взаимодействия между микросервисами.
- **Telegram Bots API**: Для создания и управления Telegram ботом.
- **PostgreSQL**: СУБД для хранения данных.
- **JUnit**: Тестирование.
- **GitHub Actions**: CI/CD для автоматизации сборки и развертывания.
- **Maven**: Управление зависимостями и сборка проекта.
- **Json-Simple**: Для работы с JSON в некоторых частях проекта.

## Структура проекта

1. **Weather API Service**: Микросервис, предоставляющий информацию о погоде. Использует RestAPI для получения запросов от других микросервисов, для отправки уведомлений использует RabbitMQ и RestAPI.
2. **User Service**: Микросервис для управления пользователями и хранения данных о пользователях, таких как их местоположение и состояние.
3. **Telegram Bot Service**: Бот для Telegram, который позволяет пользователям запрашивать информацию о погоде и получать уведомления. Использует REST API и RabbitMQ для взаимодействия с другими микросервисами.

## Установка и запуск

### 1. Настройка базы данных

Создайте базу данных PostgreSQL и пользователя:

```sql
CREATE DATABASE weatherproject;
CREATE USER username WITH LOGIN PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE weatherproject TO username;
```

Создайте таблицы и триггер:

```sql
-- Таблица для хранения данных о погоде
CREATE TABLE city_weather
(
    id          SERIAL PRIMARY KEY,
    city        VARCHAR(255),
    temperature DOUBLE PRECISION,
    condition   VARCHAR(255),
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE city_weather
    OWNER TO xyl1gan4eg;

-- Таблица для хранения данных о пользователях
CREATE TABLE tguser
(
    chat_id BIGINT NOT NULL PRIMARY KEY,
    city    VARCHAR(255),
    state   VARCHAR(255)
);

ALTER TABLE tguser
    OWNER TO xyl1gan4eg;

-- Функция для проверки изменения температуры
CREATE FUNCTION fn_check_temp_change() RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
DECLARE
    last_temp_1 FLOAT;
    last_temp_2 FLOAT;
    avg_temp FLOAT;
    diff_temp FLOAT;
    output JSON;
BEGIN
    -- Получаем последние две температуры для данного города
    SELECT temperature INTO last_temp_1
    FROM city_weather
    WHERE city = NEW.city
    ORDER BY updated_at DESC
    LIMIT 1 OFFSET 1;

    SELECT temperature INTO last_temp_2
    FROM city_weather
    WHERE city = NEW.city
    ORDER BY updated_at DESC
    LIMIT 1 OFFSET 2;

    -- Если мы нашли хотя бы две предыдущие записи, вычисляем среднюю температуру
    IF last_temp_1 IS NOT NULL AND last_temp_2 IS NOT NULL THEN
        avg_temp := (last_temp_1 + last_temp_2) / 2;

        -- Вычисляем разницу между средней температурой и новой температурой
        diff_temp := ABS(avg_temp - NEW.temperature);

        -- Если разница больше или равна 2 градусам, возвращаем JSON
        IF diff_temp >= 2 THEN
            output := JSON_BUILD_OBJECT(
                'city', NEW.city,
                'last_temp', NEW.temperature,
                'diff_temp', diff_temp
            );
            RAISE NOTICE '%', output;
        END IF;
    END IF;

    RETURN NEW;
END;
$$;

ALTER FUNCTION fn_check_temp_change() OWNER TO xyl1gan4eg;
```

### 2. Конфигурация

Настройте параметры подключения и API-ключи в файле `application.yml` для каждого микросервиса:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/weatherproject
    username: username
    password: password
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
  weather:
    api:
      url: "http://api.weatherapi.com/v1/current.json?key=${WEATHER_API_KEY}&q={city}&aqi=no"
```

### 3. Настройка Systemd

Создайте файлы для управления сервисами:

- **Weather API Service**

  ```bash
  sudo nano /etc/systemd/system/weather-api-service.service
  ```

  ```ini
  [Unit]
  Description=Weather API Service
  After=network.target

  [Service]
  User=xyl1gan4eg
  ExecStart=/usr/bin/java -jar /home/xyl1gan4eg/myJavaProjects/weatherProject/weatherApiService/target/weatherApiService-0.0.1-SNAPSHOT.jar

  SuccessExitStatus=143
  Restart=always
  RestartSec=10
  Environment=SPRING_PROFILES_ACTIVE=prod
  Environment=WEATHER_API_KEY=58b233192c024029bf3164200242607

  [Install]
  WantedBy=multi-user.target
  ```

- **User Service**

  ```bash
  sudo nano /etc/systemd/system/user-service.service
  ```

  ```ini
  [Unit]
  Description=User Service Spring Boot Application
  After=syslog.target

  [Service]
  User=xyl1gan4eg
  ExecStart=/usr/bin/java -jar /home/xyl1gan4eg/myJavaProjects/weatherProject/userService/target/userService-0.0.1-SNAPSHOT.jar

  SuccessExitStatus=143
  Restart=always
  RestartSec=10

  [Install]
  WantedBy=multi-user.target
  ```

- **Telegram Bot Service**

  ```bash
  sudo nano /etc/systemd/system/tg-bot-service.service
  ```

  ```ini
  [Unit]
  Description=Telegram bot Service
  After=network.target

  [Service]
  User=xyl1gan4eg
  ExecStart=/usr/bin/java -jar /home/xyl1gan4eg/myJavaProjects/weatherProject/tgBotService/target/TgBotService-0.0.1-SNAPSHOT.jar

  SuccessExitStatus=143
  Restart=always
  RestartSec=10
  Environment=SPRING_PROFILES_ACTIVE=prod
  Environment=TG_BOT_API_KEY=7322702173:AAHYHycNmHgeVULZEh5KWCtAHaBxqApUDS8

  [Install]
  WantedBy=multi-user.target
  ```

### 4. Запуск микросервисов

Каждый микросервис можно запустить с помощью Maven:

```bash
mvn spring-boot:run
```

### 5. Развертывание

Для автоматического развертывания используйте GitHub Actions. Определите рабочие процессы в `.github/workflows/deploy.yml`:

```yaml
name: Deploy Service

on:
  push:
    branches:
      - main

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v2

      - name: Set up SSH
        uses: webfactory/ssh-agent@v0.5.3
        with:
          ssh-private-key: ${{ secrets.SSH_PRIVATE_KEY }}

      - name: Copy source code
        uses: appleboy/scp-action@v0.1.7
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USERNAME }}
          source: ./src/
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          target: /home/username/project/

      - name: SSH into server and build project
        run: |
          ssh -o StrictHostKeyChecking=no ${SERVER_USERNAME}@${SERVER_HOST} 'bash -s' << 'EOF'
          cd /home/username/project/
          mvn clean package
          EOF

      - name: SSH into server and restart service
        run: |
          ssh -o StrictHostKeyChecking=no ${SERVER_USERNAME}@${SERVER_HOST} 'bash -s' << 'EOF'
          sudo systemctl restart my-service
          EOF
```

## Примеры использования

### Получение погоды

Отправьте запрос на `/weather/city` с параметром `city`, чтобы получить информацию о погоде в заданном городе:

```bash
curl -X GET "http://localhost:8080/weather/city?city=Paris"
```

### Отправка геолокации через Telegram бот

Используйте кнопку с запросом геолокации в Telegram бот для отправки вашей геолокации.

## Тестирование

Для тестирования используйте JUnit. Для отключения тестов при сборке используйте Maven:

```bash
mvn clean package -DskipTests
```

## Паттерны проектирования



## Паттерны проектирования

В проекте применяются следующие паттерны проектирования:
- **Команда**: Для обработки команд и коллбеков в Telegram боте.
- **Observer**: Для уведомлений о прогнозе погоды.
- **Strategy**: Для обработки разных типов запросов и команд.

## TODO

- **Улучшение кода**: Переписать и улучшить текущий код.
- **Добавление тестов**: Увеличить покрытие тестами.

---
