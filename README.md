
# OTP Service (otp-project-miphi)

Сервис для генерации и верификации одноразовых паролей (OTP) с возможностью отправки через различные каналы связи.

## 1. Обзор проекта

Сервис `otp-project-miphi` построен на Spring Boot и предназначен для отправки OTP через:
* Email
* Telegram
* SMS (через SMPP)

Он использует PostgreSQL в качестве базы данных для хранения информации о сгенерированных OTP-кодах.

## 2. Предварительные требования

* **Java JDK 17+**
* **Maven 3.6+**
* **Docker и Docker Compose** (для запуска PostgreSQL и вспомогательных сервисов)
* **Postman** (опционально, для тестирования API)
* **Актуальный Telegram Bot Token:** Если планируете тестировать отправку через Telegram
* **Telegram Chat ID:** Ваш ID для получения тестовых сообщений от бота

## 3. Установка и запуск

### 3.1 Клонирование репозитория

```bash
git clone https://github.com/your-username/otp-project-miphi.git
cd otp-project-miphi
```

### 3.2 Запуск через Docker Compose (рекомендуемый способ)

Проект содержит полную Docker-конфигурацию для запуска всех необходимых сервисов:

```bash
docker-compose up -d
```

Это запустит:
* PostgreSQL (база данных)
* MailHog (тестовый SMTP-сервер для email)
* SMPPSim (симулятор SMPP для SMS)
* Само Spring Boot приложение

### 3.3 Запуск компонентов по отдельности

#### 3.3.1 Запуск зависимостей

**PostgreSQL:**
```bash
docker-compose up -d postgres
```
База данных будет доступна на `localhost:5432` с учетными данными `user/password` и базой `otpbd`.

**MailHog (опционально для тестирования Email):**
```bash
docker run -d --name mailhog -p 1025:1025 -p 3025:1025 -p 8025:8025 mailhog/mailhog
```
Веб-интерфейс MailHog будет доступен по адресу `http://localhost:8025`.

**SMPP Simulator (опционально для тестирования SMS):**
```bash
docker run -d --name smppsim -p 2775:2775 -p 88:8080 logimethods/smppsim
```
Веб-интерфейс `smppsim` будет доступен по адресу `http://localhost:88` (логин/пароль: `smppsim`/`smppsim`).

#### 3.3.2 Запуск приложения

**Через Maven:**
```bash
mvn spring-boot:run
```

**Или через JAR-файл:**
```bash
mvn clean package -DskipTests
java -jar target/*.jar
```

#### 3.3.3 Настройте Telegram бота

- Перейдите по ссылке: [@miphi_otp_service_bot](https://t.me/miphi_otp_service_bot)
- Напишите команду `/start` боту, чтобы активировать его
- Получите chatId из ответа бота

## 4. Тестирование сервиса

### 4.1 Доступные API-эндпоинты

После запуска сервиса, API будет доступно по адресу `http://localhost:8080`.

**Основные эндпоинты:**

* `POST /api/otp/` - Генерация OTP-кода
* `POST /api/otp/validate` - Верификация OTP-кода

### 4.2 Тестирование через Postman

1. **Регистрация пользователя:**
    * Метод: POST
    * URL: `http://localhost:8080/auth/register`
    * Body (JSON):
      ```json
      {
        "username": "testuser",
        "password": "password",
        "role": "USER",
        "phoneNumber": "+79991234567",
        "email": "test@example.com",
        "telegramId": 123456
      }
      ```

2. **Вход в систему:**
    * Метод: POST
    * URL: `http://localhost:8080/auth/login`
    * Body (JSON):
      ```json
      {
        "username": "testuser",
        "password": "password"
      }
      ```

3. **Отправка OTP кода:**
   * Метод: POST
   * URL: `http://localhost:8080/otp`
   * -H "Authorization: Bearer YOUR_JWT_TOKEN" (токен получаете после регистрации и логинизации)
   * Body (JSON):
     ```json
     {
       "operationId": 111
     }
     ```

4. **Верификация OTP кода:**
   * Метод: POST
   * URL: `http://localhost:8080/otp/validate`
   * -H "Authorization: Bearer YOUR_JWT_TOKEN" (токен получаете после регистрации и логинизации)
   * Body (JSON):
     ```json
     { 
       "code": "hsfhg2"
       "operationId": 111
     }
     ```

### 4.3 Проверка отправленных сообщений

* **Email**: Откройте веб-интерфейс MailHog по адресу `http://localhost:8025`
* **SMS**: Проверьте веб-интерфейс SMPPSim по адресу `http://localhost:88`
* **Telegram**: Сообщения будут приходить напрямую в ваш Telegram (требуется настройка бота и указание правильного Chat ID)

## 5. Конфигурация

Основные настройки приложения находятся в файле `application.yaml`:

* **База данных**: настройки подключения к PostgreSQL
* **Email**: настройки SMTP-сервера
* **SMPP**: настройки для отправки SMS
* **Telegram**: токен бота
* **JWT**: настройки для генерации токенов

## 6. Архитектура проекта

Проект использует многослойную архитектуру:
* **Controller**: обработка HTTP-запросов
* **Service**: бизнес-логика
* **Repository**: доступ к данным
* **Model**: сущности и DTO

Для отправки OTP используется паттерн "Стратегия", позволяющий легко добавлять новые каналы отправки.

## 7. Остановка сервисов

* **Приложение Spring Boot:** Нажмите `Ctrl+C` в терминале, где оно запущено.
* **Docker контейнеры:**
  ```bash
  docker-compose down # Остановит и удалит контейнеры, определенные в docker-compose.yaml
  docker stop mailhog smppsim # Если запускали MailHog и SMPPsim отдельно
  docker rm mailhog smppsim   # Удалить контейнеры (опционально)
  ```
