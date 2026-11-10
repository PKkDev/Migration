# Дорожная карта изучения Java: от синтаксиса к промышленному бэкенду

* **Формат:** 1.5–2 часа в день (через день, с правом на пропуск).
* **Структура занятия:** 45 минут — глубинная теория (internals), 45–60 минут — изолированная практика руками.
* **Версии:** Java 25 (LTS), Gradle (Kotlin DSL), Spring Boot — актуальная ветка с [start.spring.io](https://start.spring.io). В старых статьях часто Spring Boot 3.x: различия сверяйте по release notes.
* **Источники:** у каждого дня есть блок «Источники» (в порядке чтения, с оценкой времени); теория дня — это чтение этих материалов плюс эксперименты в коде. Блоки добавляются по мере прохождения дней. Официальная документация — [dev.java](https://dev.java) и [docs.oracle.com/en/java/javase/25](https://docs.oracle.com/en/java/javase/25/).
* **Теория по темам:** в каждом пакете `01-java-core/src/main/java/com/sandbox/core/<тема>/` лежит `README.md` с теорией, сравнением с C# и источниками (`types`, `strings`, `collections`, `records`). Для новой темы такой файл добавляется вместе с пакетом.
* **Правило:** код пишете сами. Claude даёт советы, примеры в чате и ревью.
* **Связь с `react-app`:** итоговый проект Фазы 2 (Дни 29–35) — это бэкенд к «Enterprise Data Panel» из дорожной карты React. Договариваемся о контракте API заранее.
* **Структура кода:** темы Дней 1–13 — пакеты внутри `01-java-core` (`com.sandbox.core.<тема>`, например `strings` для Дня 2) с тестами в `src/test/java`. Отдельные Gradle-модули — только для проектов со своей конфигурацией: Task Tracker (`02-task-tracker`, День 14), Spring-проекты (`03-spring-basics` и далее).
* **Что уже есть в песочнице (`01-java-core`):** типы и Integer Cache, строки, коллекции, records, sealed и pattern matching. Эти темы в плане нужно *добить*: доработать примеры, вывод и тесты.

---

## Фаза 1. Базовый переход и фундаментальная теория (Дни 1–14)

### Неделя 1: Семантика языка и объектная модель

- [x] **День 1: Типы, память и равенство**
  - **Теория:** Примитивы против обёрток, autoboxing/unboxing, Integer Cache (-128..127). `==` сравнивает ссылки, `equals` — значения. Stack против heap. Java всегда передаёт аргументы по значению (ссылка тоже копируется). Нет `struct` и value-типов (пока). `Float.MIN_VALUE` — наименьшее *положительное* число, в отличие от C#.
  - **Практика:** Довести `TypesDemo`: печатать все секции. Воспроизвести `NullPointerException` при unboxing `null`. Показать три случая, где `==` на обёртках даёт неожиданный результат.

- [x] **День 2: Строки и текст**
  - **Теория:** Неизменяемость и String Pool, `intern()`. Compact Strings (Latin1 против UTF-16). `char` — это UTF-16 code unit, а не символ: суррогатные пары и code points. `StringBuilder` и конкатенация в цикле (под капотом `invokedynamic`).
  - **Практика:** Посчитать частоты символов в строке с эмодзи и показать, где `length()` врёт. Замерить конкатенацию 100 000 строк через `+=` и через `StringBuilder`. Текстовые блоки и `formatted`.
  - **Источники:**
    - [Strings — The Java Tutorials](https://docs.oracle.com/javase/tutorial/java/data/strings.html) (10 мин)
    - [Characters — The Java Tutorials](https://docs.oracle.com/javase/tutorial/java/data/characters.html) (5 мин)
    - [The Absolute Minimum Every Software Developer Must Know About Unicode (Joel Spolsky)](https://www.joelonsoftware.com/2003/10/08/the-absolute-minimum-every-software-developer-absolutely-positively-must-know-about-unicode-and-character-sets-no-excuses/) (20 мин)
    - [Supplementary Characters as Surrogates — The Java Tutorials](https://docs.oracle.com/javase/tutorial/i18n/text/supplementaryChars.html) (7 мин)
    - [JEP 254: Compact Strings](https://openjdk.org/jeps/254) (7 мин, читать «Summary» и «Motivation»)
    - [JEP 280: Indify String Concatenation](https://openjdk.org/jeps/280) (4 мин)
    - [Programmer's Guide to Text Blocks](https://docs.oracle.com/en/java/javase/25/text-blocks/index.html) (10 мин)
    - [Справочник `String`, Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/String.html) (справочно: `codePoints`, `intern`, `formatted`, `isBlank`)

- [x] **День 3: Классы, наследование, интерфейсы**
  - **Теория:** Все методы виртуальные (нет `virtual`/`override`), `final`, `abstract`, `default`-методы интерфейсов. Порядок инициализации: static-блок, поля, конструктор. Package-private вместо `internal`. Нет свойств: геттеры и сеттеры.
  - **Практика:** `abstract class Shape`, `Circle`, `Rectangle`, интерфейс `Printable` с `default`-методом, `List<Shape>`. Воспроизвести баг: вызов переопределяемого метода из конструктора базового класса.
  - **Источники** (теория и сравнение с C# собраны в [oop/README.md](01-java-core/src/main/java/com/sandbox/core/oop/README.md)):
    - [Inheritance, Java Tutorials](https://docs.oracle.com/javase/tutorial/java/IandI/subclasses.html) (10 мин)
    - [Overriding and Hiding Methods, Java Tutorials](https://docs.oracle.com/javase/tutorial/java/IandI/override.html) (10 мин)
    - [Polymorphism, Java Tutorials](https://docs.oracle.com/javase/tutorial/java/IandI/polymorphism.html) (8 мин)
    - [Writing Final Classes and Methods, Java Tutorials](https://docs.oracle.com/javase/tutorial/java/IandI/final.html) (4 мин)
    - [Abstract Methods and Classes, Java Tutorials](https://docs.oracle.com/javase/tutorial/java/IandI/abstract.html) (8 мин)
    - [Default Methods, Java Tutorials](https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html) (8 мин)
    - [Initializing Fields, Java Tutorials](https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html) (8 мин)
    - [Controlling Access to Members of a Class, Java Tutorials](https://docs.oracle.com/javase/tutorial/java/javaOO/accesscontrol.html) (5 мин)
    - [JEP 513: Flexible Constructor Bodies](https://openjdk.org/jeps/513) (7 мин, новое в Java 25)
    - [Polymorphism, C# (Microsoft Learn)](https://learn.microsoft.com/en-us/dotnet/csharp/fundamentals/object-oriented/polymorphism) (10 мин, для сравнения)

- [x] **День 4: Контракт `equals` / `hashCode` / `compareTo`**
  - **Теория:** Контракт `equals`/`hashCode` и что ломается при его нарушении. Изменяемые поля в ключе `HashMap`. `Comparable` против `Comparator`. Идентичность и `identityHashCode`.
  - **Практика:** Положить объекты без `hashCode` в `HashSet` и увидеть дубли. Изменить ключ после `put` и «потерять» запись. Отсортировать список через `Comparator.comparing(...).thenComparing(...)`.
  - **Источники** (теория и сравнение с C# собраны в [equality/README.md](01-java-core/src/main/java/com/sandbox/core/equality/README.md)):
    - [Object as a Superclass, Java Tutorials](https://docs.oracle.com/javase/tutorial/java/IandI/objectclass.html) (10 мин)
    - [`Object.equals` и `Object.hashCode`, Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Object.html) (10 мин)
    - [Object Ordering, Java Tutorials](https://docs.oracle.com/javase/tutorial/collections/interfaces/order.html) (10 мин)
    - [`Comparator`, Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Comparator.html) (10 мин)
    - [`Comparable`, Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Comparable.html) (5 мин)
    - [`Map`, Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Map.html) (3 мин, предупреждение про изменяемые ключи)
    - [Lombok: `@EqualsAndHashCode`](https://projectlombok.org/features/EqualsAndHashCode) (5 мин)
    - [How to define value equality for a type, C# (Microsoft Learn)](https://learn.microsoft.com/en-us/dotnet/csharp/programming-guide/statements-expressions-operators/how-to-define-value-equality-for-a-type) (10 мин, для сравнения)

- [x] **День 5: Исключения**
  - **Теория:** Checked против unchecked, `throws`. `try-with-resources` и `AutoCloseable`, suppressed exceptions. Ловушки `finally`. Цепочки `cause`. Почему нельзя глотать исключения. Как это соотносится с `using`/`try-catch` в C#.
  - **Практика:** Иерархия собственных исключений. Ресурс, у которого `close()` тоже бросает исключение, и вывод suppressed. Обернуть checked-исключение в unchecked с сохранением `cause`.
  - **Источники** (теория и сравнение с C# собраны в [exceptions/README.md](01-java-core/src/main/java/com/sandbox/core/exceptions/README.md)):
    - [Lesson: Exceptions, Java Tutorials](https://docs.oracle.com/javase/tutorial/essential/exceptions/index.html) (10 мин)
    - [The Catch or Specify Requirement](https://docs.oracle.com/javase/tutorial/essential/exceptions/catch.html) (7 мин)
    - [The finally Block](https://docs.oracle.com/javase/tutorial/essential/exceptions/finally.html) (5 мин)
    - [The try-with-resources Statement](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html) (10 мин)
    - [Unchecked Exceptions: The Controversy](https://docs.oracle.com/javase/tutorial/essential/exceptions/runtime.html) (5 мин)
    - [Creating Exception Classes](https://docs.oracle.com/javase/tutorial/essential/exceptions/creating.html) (5 мин)
    - [Chained Exceptions](https://docs.oracle.com/javase/tutorial/essential/exceptions/chained.html) (4 мин)
    - [Exception handling, C# (Microsoft Learn)](https://learn.microsoft.com/en-us/dotnet/csharp/fundamentals/exceptions/) (10 мин, для сравнения)
    - [Best practices for exceptions, .NET (Microsoft Learn)](https://learn.microsoft.com/en-us/dotnet/standard/exceptions/best-practices-for-exceptions) (10 мин, для сравнения)

- [ ] **День 6: Дженерики и стирание типов**
  - **Теория:** Type erasure: в рантайме нет `List<String>`, есть `List`. Нет `List<int>` (в C# дженерики reified). Wildcards и правило PECS: `? extends` / `? super`. Heap pollution.
  - **Практика:** Написать `Pair<A, B>` и `Result<T>`. Метод `copy(List<? super T> dst, List<? extends T> src)`. Показать через `getClass()`, что `List<String>` и `List<Integer>` — один класс.
  - **Источники** (теория и сравнение с C# собраны в [generics/README.md](01-java-core/src/main/java/com/sandbox/core/generics/README.md)):
    - [Generic Types](https://docs.oracle.com/javase/tutorial/java/generics/types.html) (7 мин)
    - [Generic Methods](https://docs.oracle.com/javase/tutorial/java/generics/methods.html) (5 мин)
    - [Bounded Type Parameters](https://docs.oracle.com/javase/tutorial/java/generics/bounded.html) (7 мин)
    - [Generics, Inheritance, and Subtypes](https://docs.oracle.com/javase/tutorial/java/generics/inheritance.html) (5 мин)
    - [Upper Bounded Wildcards](https://docs.oracle.com/javase/tutorial/java/generics/upperBounded.html) (5 мин)
    - [Lower Bounded Wildcards](https://docs.oracle.com/javase/tutorial/java/generics/lowerBounded.html) (4 мин)
    - [Wildcard Guidelines](https://docs.oracle.com/javase/tutorial/java/generics/wildcardGuidelines.html) (5 мин, PECS)
    - [Type Erasure](https://docs.oracle.com/javase/tutorial/java/generics/erasure.html) (10 мин)
    - [Restrictions on Generics](https://docs.oracle.com/javase/tutorial/java/generics/restrictions.html) (10 мин)
    - [Non-Reifiable Types, Heap Pollution](https://docs.oracle.com/javase/tutorial/java/generics/nonReifiableVarargsType.html) (7 мин)
    - [Covariance and contravariance, .NET (Microsoft Learn)](https://learn.microsoft.com/en-us/dotnet/standard/generics/covariance-and-contravariance) (10 мин, для сравнения)

- [ ] **День 7: Практикум недели — моделирование домена**
  - **Теория:** `record`, `enum` с полями и методами, `sealed` + pattern matching, `switch`-выражения, `Optional` (когда уместен, когда нет).
  - **Практика:** Мини-модель `Order`, `OrderStatus`, `Money` на records и sealed. Парсер текстовых команд на pattern matching. Покрыть JUnit-тестами.

---

### Неделя 2: Внутренности JVM, коллекции, стримы и конкурентность

- [ ] **День 8: JVM изнутри — байткод, ClassLoader, JIT, память**
  - **Теория:** От `.java` до байткода и исполнения. ClassLoader, верификация. Интерпретатор и JIT (C1/C2). Области памяти: heap (молодое/старое поколение), metaspace, stack. Сборщики мусора (G1, ZGC) на уровне идеи. Сравнение с CLR.
  - **Практика:** `javap -c` на простом классе. Запустить с `-Xlog:gc` и увидеть сборки. Посмотреть процесс через `jcmd` и `jconsole`.

- [ ] **День 9: Внутренности `ArrayList` и `HashMap`**
  - **Теория:** Рост массива в `ArrayList`. `HashMap`: корзины, коллизии, treeify, load factor, resize. Fail-fast итераторы и `ConcurrentModificationException`. Почему `LinkedList` почти всегда проигрывает.
  - **Практика:** Написать свой упрощённый `ArrayList` и `HashMap` на массивах. Воспроизвести `ConcurrentModificationException` и исправить через `removeIf`.

- [ ] **День 10: Stream API против LINQ**
  - **Теория:** Ленивость, промежуточные и терминальные операции. Стрим одноразовый (в отличие от `IEnumerable`). `collect`, `groupingBy`, `partitioningBy`, `toMap`, `flatMap`, `reduce`. Ловушки `parallelStream()`.
  - **Практика:** Переписать 10 своих LINQ-запросов из C# (GroupBy, Join, OrderBy/ThenBy, Aggregate, Distinct) на Stream API. Поймать `IllegalStateException` при повторном использовании стрима.

- [ ] **День 11: Лямбды, функциональные интерфейсы и замыкания**
  - **Теория:** `Function`, `Supplier`, `Predicate`, `Consumer`, ссылки на методы. Лямбда захватывает только effectively final значения (в C# захватываются переменные). `Optional`: `map`, `flatMap`, `orElseGet`.
  - **Практика:** Написать `compose` и `curry` для `Function`. Воспроизвести ошибку effectively final и обойти её тремя способами.

- [ ] **День 12: Потоки и модель памяти (JMM)**
  - **Теория:** `Thread`, `Runnable`, `synchronized`, `volatile`, happens-before. Гонки данных, deadlock. `ExecutorService` и пулы потоков.
  - **Практика:** Счётчик с гонкой, затем исправление через `synchronized`, `AtomicInteger` и `LongAdder`. Воспроизвести deadlock двух замков и снять дамп через `jstack`.

- [ ] **День 13: `CompletableFuture` и виртуальные потоки**
  - **Теория:** `CompletableFuture` против `Task`/`async-await`: `thenApply`, `thenCompose`, `allOf`, обработка ошибок. Виртуальные потоки (Project Loom): платформенные и виртуальные потоки, pinning. `ConcurrentHashMap`.
  - **Практика:** Параллельно опросить три «сервиса» с таймаутом и fallback. Запустить 10 000 задач с `sleep` на пуле платформенных потоков и на виртуальных, сравнить время.

- [ ] **День 14: Итоговый проект первой фазы**
  - **Практика:** Консольное приложение «Task Tracker» с хранением в памяти: records, sealed-иерархия команд, Stream API, собственные исключения, чистая Java (без Spring, из библиотек только JUnit). Покрытие тестами основной логики. Оформить как модуль `02-task-tracker`.

---

## Фаза 2. Индустриальный стек (Дни 15–35)

### Неделя 3: Spring Boot — контейнер, REST и «магия» под капотом

- [ ] **Дни 15–16: Gradle, IoC-контейнер и внедрение зависимостей**
  - **Теория:** Конфигурации Gradle (`implementation`, `compileOnly`, `runtimeOnly`, `annotationProcessor`). `ApplicationContext`, жизненный цикл бина, область видимости (по умолчанию singleton). Внедрение через конструктор. `@Configuration` и `@Bean`. Сравнение с DI в ASP.NET Core (`AddSingleton`/`AddScoped`/`AddTransient`).
  - **Практика:** Создать модуль `03-spring-basics` через start.spring.io. Описать сервисы с зависимостями, профили, `@ConfigurationProperties`. Создать контекст вручную без Boot и посмотреть порядок инициализации бинов.

- [ ] **Дни 17–18: REST-контроллеры, валидация и обработка ошибок**
  - **Теория:** `@RestController`, `@GetMapping`/`@PostMapping`, DTO на records, `ResponseEntity`. Bean Validation (`@Valid`, `@NotBlank`, `@Size`): аннотации срабатывают только при проверке валидатором. Jackson. Глобальный обработчик `@RestControllerAdvice` и формат `ProblemDetails` (RFC 9457).
  - **Практика:** CRUD для сущности `Task` в памяти. Единый формат ошибок с перечнем полей (его потом разберёт Zod-форма в React). Подключить OpenAPI (springdoc).

- [ ] **Дни 19–21: Прокси, AOP и `@Transactional`**
  - **Теория:** Как Spring оборачивает бины в прокси (JDK и CGLIB). AOP: аспекты, advice, pointcut. Автоконфигурация Boot (`@Conditional...`). Почему `@Transactional` не срабатывает при вызове метода внутри того же класса (self-invocation). Filter против Interceptor.
  - **Практика:** Написать аспект, который логирует время выполнения методов с помощью своей аннотации. Воспроизвести баг self-invocation и исправить его.

---

### Неделя 4: Данные и тестирование

- [ ] **Дни 22–24: JPA и Hibernate**
  - **Теория:** Жизненный цикл сущности, persistence context, dirty checking. `LAZY` против `EAGER`, проблема N+1, `@EntityGraph`, `JOIN FETCH`. Проекции в DTO. Сопоставление с EF Core (change tracker, `Include`, `AsNoTracking`).
  - **Практика:** PostgreSQL в Docker. Сущности `Project` и `Task` со связью один-ко-многим. Включить логирование SQL, воспроизвести N+1, исправить через fetch join.

- [ ] **Дни 25–26: Spring Data, пагинация и миграции Flyway**
  - **Теория:** `JpaRepository`, derived-запросы, `@Query`, `Pageable` и `Sort`, `Specification`. Миграции Flyway (аналог EF Migrations): версионирование, почему нельзя править применённые миграции. Транзакции: propagation и isolation, оптимистичная блокировка `@Version` (аналог concurrency token).
  - **Практика:** Миграции `V1__...`, `V2__...`. Метод поиска с пагинацией, серверной сортировкой и фильтром. Конфликт версий двух параллельных обновлений и ответ `409`.

- [ ] **Дни 27–28: Тестирование**
  - **Теория:** JUnit 5, Mockito, AssertJ. Срезовые тесты (`@WebMvcTest`, `@DataJpaTest`). Testcontainers вместо in-memory БД. Пирамида тестов.
  - **Практика:** Тесты сервиса с моками, тест контроллера (включая формат ошибок валидации), интеграционный тест репозитория на настоящем PostgreSQL через Testcontainers.

---

### Неделя 5: Инженерный проект (Enterprise Data API)

- [ ] **Дни 29–35: Бэкенд для Enterprise Data Panel**
  - **Стек:** Java 25, Spring Boot, Spring Data JPA, PostgreSQL, Flyway, Bean Validation, springdoc, JUnit 5 + Testcontainers, Docker Compose.
  - **Требования к функционалу:**
    - Список с серверной пагинацией, сортировкой и поиском (контракт согласован с таблицей в React).
    - Создание и редактирование с валидацией и ответами 400 в едином формате `ProblemDetails`.
    - «Мягкое» удаление с эндпоинтом восстановления (для оптимистичного удаления с отменой в UI).
    - Оптимистичная блокировка: конфликт версий возвращает 409.
    - Единая обработка ошибок 400/404/409/500 и настройка CORS для `react-app`.
    - OpenAPI-описание, из которого можно сгенерировать TypeScript-клиент.
    - Интеграционные тесты на Testcontainers, запуск всего окружения одной командой `docker compose up`.

---

## Фаза 3. Продвинутый уровень и масштабирование (Дни 36–50)

### Неделя 6: Производительность и эксплуатация

- [ ] **Дни 36–38: Профилирование и диагностика JVM**
  - **Теория:** Java Flight Recorder и Mission Control, heap dump и поиск утечек, логи GC. Настройка пула соединений (HikariCP). Типичные причины медленных запросов.
  - **Практика:** Создать искусственно медленный эндпоинт и утечку памяти. Найти узкие места через JFR и heap dump, исправить.

- [ ] **Дни 39–40: Кэширование**
  - **Теория:** Абстракция Spring Cache (`@Cacheable`, `@CacheEvict`), Caffeine (локальный кэш), Redis (распределённый). Инвалидация, TTL, cache stampede.
  - **Практика:** Закэшировать тяжёлый запрос справочника через Caffeine, затем перенести на Redis. Замерить выигрыш и проверить инвалидацию при записи.

- [ ] **Дни 41–42: Наблюдаемость**
  - **Теория:** Spring Boot Actuator, метрики (Micrometer), структурированные логи, корреляция запросов (trace id), health checks.
  - **Практика:** Подключить Actuator и Prometheus-формат метрик, добавить свою метрику и сквозной идентификатор запроса в логах.

---

### Неделя 7: Безопасность, асинхронность и подготовка к интервью

- [ ] **Дни 43–45: Spring Security**
  - **Теория:** Цепочка фильтров безопасности. Аутентификация и авторизация, JWT, OAuth2 Resource Server. CSRF и CORS. Сопоставление с ASP.NET Core Identity/JWT Bearer.
  - **Практика:** Защитить API токенами JWT, добавить роли и проверки прав на уровне методов. Связать с `react-app`: логин, хранение токена, обработка 401/403.

- [ ] **Дни 46–48: Асинхронность и события**
  - **Теория:** `@Async`, события приложения, транзакционные слушатели. Идея Transactional Outbox. Обзор брокеров сообщений (Kafka/RabbitMQ), идемпотентность потребителей.
  - **Практика:** Публиковать доменное событие при создании задачи и обрабатывать его после коммита. Реализовать упрощённый outbox в таблице БД.

- [ ] **Дни 49–50: Систематизация знаний и подготовка к интервью**
  - **Теория:** Повторение ключевых тем:
    - Контракт `equals`/`hashCode`, `HashMap` и неизменяемость.
    - JVM: память, GC, JIT.
    - Конкурентность: JMM, `CompletableFuture`, виртуальные потоки.
    - Spring: контейнер, прокси, `@Transactional`.
    - JPA: N+1, `LAZY`, оптимистичные блокировки.
    - Тестирование: срезы и Testcontainers.

---

## Шпаргалка: .NET → Java

| Задача | .NET | Java |
|---|---|---|
| Основной фреймворк | ASP.NET Core | Spring Boot (де-факто стандарт) |
| Работа с БД (ORM) | Entity Framework Core | Spring Data JPA (Hibernate) / jOOQ / MyBatis |
| Стримы данных | LINQ (Where, Select, Aggregate) | Stream API (filter, map, reduce) |
| Сборка проекта | .csproj / MSBuild | Gradle (Kotlin/Groovy DSL) или Maven (pom.xml) |
| Неизменяемые DTO | `record Point(int X, int Y);` | `record Point(int x, int y) {}` |
| Генерация бойлерплейта | Свойства C#, Source Generators | Lombok (`@RequiredArgsConstructor`, `@Builder`) |
| Управление зависимостями | NuGet | Maven Central (через Gradle / Maven) |
| Миграции БД | EF Core Migrations | Liquibase или Flyway |

### Практический план адаптации

Не пытайтесь зубрить Java с нуля по книгам «для начинающих». Вы уже опытный инженер. Ваш стек перехода — это Java 17/21+ и Spring Boot:

**Быстро пройтись по синтаксическим различиям (3–4 дня):**
- Заменители свойств: как писать `record` и как работает Lombok.
- Коллекции (`List`, `Map`, `Set`) и работа со Stream API.
- Исключения (в Java есть Checked Exceptions, в отличие от C#, хотя в Spring их почти не форсируют).

**Освоить Spring Boot (основной фокус):**
- Аннотации DI: `@Component`, `@Service`, `@Repository`, `@Autowired` (а точнее, внедрение через конструктор).
- Контроллеры: `@RestController`, `@GetMapping`, `@PostMapping`.
- Работа с БД: `@Entity`, репозитории `JpaRepository<T, ID>` (работают по аналогии с паттерном Repository, где методы вида `findByEmail` генерируются на лету).

**Использовать AI как транслятор:**
- Берите кусок своего старого кода на C# и просите: «Перепиши этот сервис с EF Core на идиоматичный Spring Boot + Spring Data JPA с использованием records и Lombok». Вы сразу увидите точные аналогии.
