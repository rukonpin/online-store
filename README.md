# Online Store

Веб-приложение «Витрина интернет-магазина» на Spring Boot — учебный проект

[![Release](https://img.shields.io/badge/release-v1.0-blue)](https://github.com/rukonpin/online-store/releases/tag/v1.0)
[![Release](https://img.shields.io/badge/release-v2.0-blue)](https://github.com/rukonpin/online-store/releases/tag/v2.0)

> ⚡ Приложение полностью переведено на реактивный стек: Spring WebFlux + Spring Data R2DBC

Пользователи могут просматривать каталог товаров, добавлять их в корзину и оформлять заказы, просматривать историю 
заказов. Поддерживается 
регистрация, вход и сессионная авторизация.

## Showcase

> 📹 **Видео-демонстрация проекта:**

[![Showcase](https://img.youtube.com/vi/nLFn27XQWGs/maxresdefault.jpg)](https://youtu.be/nLFn27XQWGs)

## Стек технологий

| Слой              | Технология                                             |
|-------------------|---------------------------------------------------------|
| Backend           | Java 21, Spring Boot 3                                  |
| Web               | Spring WebFlux, Thymeleaf                                |
| БД                | PostgreSQL 15, Spring Data R2DBC                          |
| Маппинг           | MapStruct                                                |
| Тесты             | JUnit 5, Mockito, Reactor Test (StepVerifier), Spring Boot Test, WebFluxTest, Testcontainers |
| Сборка            | Maven                                                    |
| Контейнеризация   | Docker, Docker Compose                                   |


## Схема базы данных

![Схема БД](docs/db-schema.jpg)


## Структура проекта

```
src/
├── main/
│   ├── java/com/online/store/
│   │   ├── config/
│   │   │   ├── R2dbcConfig.java       # @EnableR2dbcAuditing
│   │   │   └── WebFluxConfig.java     # реактивный резолвер Pageable
│   │   ├── controller/
│   │   │   ├── cart/
│   │   │   │   ├── api/     # CartRestController
│   │   │   │   └── web/     # CartViewController
│   │   │   ├── order/
│   │   │   │   ├── api/     # OrderRestController
│   │   │   │   └── web/     # OrderViewController
│   │   │   ├── product/
│   │   │   │   ├── api/     # ProductRestController
│   │   │   │   └── web/     # ProductViewController
│   │   │   └── user/
│   │   │       ├── api/     # UserRestController
│   │   │       └── web/     # UserViewController
│   │   ├── dto/
│   │   │   ├── cart/        # CartDto, CartItemDto, UpdateItemQuantityDto
│   │   │   ├── order/       # OrderDto, OrderItemDto
│   │   │   ├── product/     # ProductDto
│   │   │   └── user/        # UserLoginDto, UserRegistrationDto
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── ValidationException.java
│   │   │   ├── cart/, order/, product/, user/   # кастомные исключения
│   │   ├── mapper/          # MapStruct: CartMapper, OrderMapper, ProductMapper, UserMapper
│   │   ├── model/
│   │   │   ├── ErrorResponse.java
│   │   │   ├── user/, product/, order/, cart/   # R2DBC-сущности (Persistable<UUID>)
│   │   ├── repository/      # ReactiveCrudRepository / R2dbcRepository
│   │   ├── service/         # Интерфейсы и реактивные реализации сервисов (Mono/Flux)
│   │   └── StoreApplication.java
│   └── resources/
│       ├── static/          # CSS, JS, иконки
│       ├── templates/       # Thymeleaf-шаблоны
│       ├── schema.sql       # DDL таблиц
│       ├── data-dev.sql     # тестовые данные для dev-профиля
│       ├── application.yaml
│       ├── application-dev.yaml
│       └── application-test.yaml
└── test/
    └── java/com/online/store/
        ├── BaseIntegrationTest.java     # база для интеграционных тестов (Testcontainers)
        ├── StoreIntegrationTest.java    # E2E: регистрация → вход → корзина → заказ
        ├── controller/       # @WebFluxTest тесты контроллеров
        └── service/          # unit-тесты сервисов (Mockito + StepVerifier)
```


## Запуск

### Требования

- Java 21+
- Maven 3.9+
- Docker и Docker Compose (для запуска в контейнере и для интеграционных тестов на Testcontainers)
- PostgreSQL 15 с реактивным драйвером r2dbc-postgresql (можно поднять через Docker Compose)

### 1. Локально в IDE

**Шаг 1.** Клонируй репозиторий:

```bash
git clone https://github.com/rukonpin/online-store.git
cd online-store
```

**Шаг 2.** Запусти PostgreSQL через Docker Compose (только БД):

```bash
docker compose -f docker-compose-dev.yml up -d
```

**Шаг 3.** Запусти приложение с профилем `dev`:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Приложение будет доступно по адресу: [http://localhost:8080/products](http://localhost:8080/products)

### 2. Сборка и запуск JAR

```bash
# Сборка
mvn clean package -DskipTests

# Запуск (предварительно должна быть запущена БД)
java -jar target/*.jar --spring.profiles.active=dev
```

### 3. Запуск в Docker (полный стек)

Запускает и приложение, и базу данных одной командой:

```bash
docker compose up --build
```

Приложение доступно по адресу: [http://localhost:8080/products](http://localhost:8080/products)

Остановка:

```bash
docker compose down
```

Остановка с удалением данных БД:

```bash
docker compose down -v
```

## Тесты

Покрытие тестами: **82%**

```bash
mvn test
```

## API

### Авторизация

| Метод | Путь                  | Описание              |
|-------|-----------------------|-----------------------|
| POST  | `/api/auth/register`  | Регистрация           |
| POST  | `/api/auth/login`     | Вход (создаёт сессию) |
| POST  | `/api/auth/logout`    | Выход                 |

### Товары

| Метод | Путь                  | Описание                        |
|-------|-----------------------|---------------------------------|
| GET   | `/api/products`       | Список товаров (поиск, пагинация)|
| GET   | `/api/products/{uuid}`| Товар по UUID                   |

### Корзина

| Метод  | Путь                        | Описание                    |
|--------|-----------------------------|-----------------------------|
| GET    | `/api/cart`                 | Получить корзину            |
| POST   | `/api/cart/items`           | Добавить товар              |
| PUT    | `/api/cart/items/{itemUuid}`| Изменить количество         |
| DELETE | `/api/cart/items/{itemUuid}`| Удалить позицию             |
| DELETE | `/api/cart`                 | Очистить корзину            |

### Заказы

| Метод | Путь                   | Описание              |
|-------|------------------------|-----------------------|
| POST  | `/api/orders`          | Оформить заказ        |
| GET   | `/api/orders/{orderUuid}`   | Получить заказ по UUID|

