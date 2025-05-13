# otp-project-miphi: Инструкция по запуску и тестированию

Это руководство поможет вам настроить, запустить и протестировать сервис `otp-project-miphi` для генерации и верификации одноразовых паролей (OTP).

## 1. Обзор проекта

Сервис `otp-project-miphi` построен на Spring Boot и предназначен для отправки OTP через:
*   Email
*   Telegram
*   SMS (через SMPP)

Он использует PostgreSQL в качестве базы данных.

## 2. Предварительные требования

*   **Java JDK 17+**
*   **Maven 3.6+**
*   **Docker и Docker Compose** (для запуска PostgreSQL и опционально MailHog, SMPP Simulator)
*   **Postman** (опционально, для тестирования API)
*   **Актуальный Telegram Bot Token:** Если планируете тестировать отправку через Telegram.
*   **Telegram Chat ID:** Ваш ID для получения тестовых сообщений от бота.

## 3. Конфигурация

Основная конфигурация находится в файле `src/main/resources/application.yaml`.

**Ключевые параметры, на которые стоит обратить внимание и при необходимости изменить:**

*   **`spring.datasource`**: Настройки подключения к PostgreSQL. Текущие настройки (`url: jdbc:postgresql://localhost:5432/otpbd`, `username: user`, `password: password`) соответствуют `docker-compose.yaml`.
*   **`spring.mail`**: Настроено на `host: localhost` и `port: 3025`. Идеально для локального SMTP-сервера типа MailHog.
*   **`spring.telegram.bot.token`**: **ВАЖНО!** Замените значение `7883204822:AAHO7qt41rOE6PRSiU9C-h1IKTn1may94Tg` на ваш реальный токен Telegram бота.
*   **`spring.smpp`**: Настройки для SMPP (для SMS). Если у вас нет локального SMPP-симулятора, этот канал не будет работать без дополнительной настройки.

**Рекомен��ация:** Для локальной разработки и переопределения конфигурационных параметров без изменения основного `application.yaml`, вы можете создать файл `src/main/resources/application-local.yaml` и активировать профиль `local` при запуске. Либо используйте переменные окружения.

Например, чтобы переопределить токен Telegram для локального запуска:
Создайте `src/main/resources/application-local.yaml`:
```yaml
spring:
  telegram:
    bot:
      token: YOUR_ACTUAL_TELEGRAM_BOT_TOKEN_HERE
      # chat-id: YOUR_TELEGRAM_CHAT_ID_FOR_TESTING (можно добавить, если нет глобального)
```
И запускайте приложение с профилем `local`: `java -jar target/otp-project-miphi-*.jar --spring.profiles.active=local`

## 4. Запуск проекта

### Шаг 1: Запуск зависимостей

**1.1. Запуск PostgreSQL:**
Проект содержит файл `docker-compose.yaml` для запуска PostgreSQL.
Откройте терминал в корневой директории проекта и выполните:
```bash
docker-compose up -d postgres
```
Это запустит контейнер PostgreSQL в фоновом режиме. База данных будет доступна на `localhost:5432` с учетными данными `user/password` и базой `otpbd`, как указано в `application.yaml`.

**1.2. (Опционально) Запуск MailHog для тестирования Email:**
MailHog - это простой SMTP-сервер с веб-интерфейсом для просмотра отправленных писем. `application.yaml` настроен на отправку почты на `localhost:3025`.
Запустите MailHog через Docker:
```bash
docker run -d --name mailhog -p 1025:1025 -p 3025:1025 -p 8025:8025 mailhog/mailhog
```
*   `-p 3025:1025`: Перенаправляет SMTP-трафик с порта 3025 хоста на порт 1025 контейнера MailHog.
*   `-p 8025:8025`: Веб-интерфейс MailHog будет доступен по адресу `http://localhost:8025`.

**1.3. (Опционально) Запуск SMPP Simulator для тестирования SMS:**
`application.yaml` настроен на SMPP-сервер на `localhost:2775`. Вы можете использовать SMPP-��имулятор, например, `smppsim`.
Запустите `smppsim` через Docker:
```bash
docker run -d --name smppsim -p 2775:2775 -p 88:8080 logimethods/smppsim
```
*   Веб-интерфейс `smppsim` будет доступен по адресу `http://localhost:88` (логин/пароль по умолчанию: `smppsim`/`smppsim`).

### Шаг 2: Сборка и запуск приложения Spring Boot

1.  Откройте терминал в корневой директории проекта.
2.  Соберите проект с помощью Maven:
    ```bash
    mvn clean package -DskipTests
    ```
    (`-DskipTests` можно убрать, если тесты настроены и должны выполняться при сборке).
3.  Запустите собранное приложение:
    ```bash
    java -jar target/otp-project-miphi-*.jar
    ```
    (Замените `*` на актуальную версию артефакта, например, `1.0-SNAPSHOT`).
    Если вы создали `application-local.yaml` и хотите его использовать:
    ```bash
    java -jar target/otp-project-miphi-*.jar --spring.profiles.active=local
    ```

Приложение должно запуститься на порту `8080` (согласно `application.yaml`).

## 5. Тестирование API

После успешного запуска приложения вы можете протестировать его API.

### 5.1. Тестирование через Swagger UI

Swagger UI предоставляет интерактивную документацию для вашего API.

1.  Откройте в браузере: `http://localhost:8080/swagger-ui.html`
2.  Вы увидите список доступных контроллеров (например, `OtpController` или аналогичный).

**Пример: Отправка OTP через Email (для просмотра в MailHog)**

*   Найдите эндпоинт для отправки OTP (например, `POST /api/otp/send` или аналогичный).
*   Нажмите на него, чтобы развернуть детали.
*   Нажмите кнопку "Try it out".
*   В поле "Request body" введите JSON. Точная структура зависит от вашего DTO, но пример может быть таким:
    ```json
    {
      "channel": "EMAIL",
      "destination": "test@example.com", // Любой email, он появится в MailHog
      "userId": "user123" // Или другой идентификатор, если требуется
    }
    ```
*   Нажмите кнопку "Execute".
*   Проверьте ответ. В случае успеха (например, статус `200 OK` или `201 Created`), вы должны получить `otpId`.
*   Откройте MailHog (`http://localhost:8025`) и проверьте наличие письма с OTP кодом для `test@example.com`.

**Пример: Верификация OTP**

*   Найдите эндпоинт для верификации OTP (например, `POST /api/otp/verify`).
*   Нажмите "Try it out".
*   В поле "Request body" введите JSON, используя `otpId` из предыдущего шага и код, полученный из письма в MailHog:
    ```json
    {
      "otpId": "полученный_otpId_из_ответа_на_send",
      "code": "код_из_письма"
    }
    ```
*   Нажмите "Execute".
*   Проверьте ответ (например, статус `200 OK` и сообщение об успешной верификации).

### 5.2. Тестирование через Postman

1.  **Настройка Postman:**
    *   Создайте новую коллекцию для проекта.
    *   Вы можете добавить переменную окружения `baseUrl` со значением `http://localhost:8080`.

**Запрос на отправку OTP (Send OTP):**

*   **Метод:** `POST`
*   **URL:** `{{baseUrl}}/ПУТЬ_К_ЭНДПОИНТУ_ОТПРАВКИ_OTP` (например, `{{baseUrl}}/api/otp/send`)
*   **Headers:**
    *   `Content-Type`: `application/json`
*   **Body (raw, JSON):**

    *   **Для Email (проверка через MailHog):**
        ```json
        {
          "channel": "EMAIL",
          "destination": "another-test@example.com",
          "userId": "user456"
        }
        ```
        *Ожидаемый результат:* Письмо в MailHog (`http://localhost:8025`).

    *   **Для Telegram (требует настроенный токен и ваш Chat ID):**
        ```json
        {
          "channel": "TELEGRAM",
          "destination": "YOUR_TELEGRAM_CHAT_ID", // Замените на ваш реальный Chat ID
          "userId": "user789"
        }
        ```
        *Ожидаемый результат:* Сообщение с OTP кодом в вашем Telegram чате.

    *   **Для SMS (проверка через SMPP Simulator, если запущен):**
        ```json
        {
          "channel": "SMS",
          "destination": "79001234567", // Номер телефона получателя
          "userId": "user101"
        }
        ```
        *Ожидаемый результат:* Сообщение в SMPP Simulator (`http://localhost:88`).

*   **Отправьте запрос.** В ответе вы должны получить `otpId`. Сохраните его.

**Запрос на верификацию OTP (Verify OTP):**

*   **Метод:** `POST`
*   **URL:** `{{baseUrl}}/ПУТЬ_К_ЭНДПОИНТУ_ВЕРИФИКАЦИИ_OTP` (например, `{{baseUrl}}/api/otp/verify`)
*   **Headers:**
    *   `Content-Type`: `application/json`
*   **Body (raw, JSON):**
    ```json
    {
      "otpId": "otpId_из_предыдущего_запроса",
      "code": "КОД_ИЗ_СООБЩЕНИЯ_EMAIL_TELEGRAM_SMS"
    }
    ```
*   **Отправьте запрос.**
    *   **Успешный ответ (200 OK):** Сообщение об успешной верификации.
    *   **Неверный код (400 Bad Request или другой код ошибки):** Сообщение об ошибке.

## 6. Проверка результатов по каналам

*   **Email:** Откройте веб-интерфейс MailHog (`http://localhost:8025`) и найдите отправленное письмо.
*   **Telegram:** Проверьте ваш Telegram-чат, на который был настроен бот и указан `destination` (Chat ID).
*   **SMS:** Откройте веб-интерфейс SMPP-симулятора (`http://localhost:88` для `logimethods/smppsim`) и проверьте полученные сообщения.

## 7. Остановка сервисов

*   **Приложение Spring Boot:** Нажмите `Ctrl+C` в терминале, где оно запущено.
*   **Docker контейнеры:**
    ```bash
    docker-compose down # Остановит и удалит контейнер postgres, определенный в docker-compose.yaml
    docker stop mailhog smppsim # Если запускали MailHog и SMPPsim
    docker rm mailhog smppsim   # Удалить контейнеры (опционально)
    ```

Эта инструкция должна помочь вам запустить и протестировать ваш проект `otp-project-miphi` на основе предоставленных файлов. Убедитесь, что все внешние зависимости (токены, доступность сервисов) настроены корректно.