# Исключения

Код этого пакета пишете вы по заданиям Дня 5. Здесь теория, сравнение с C# и источники. Все примеры ниже проверены запуском
на JDK 25.

## Иерархия

```
Throwable
├── Error                      ← сбои JVM (OutOfMemoryError, StackOverflowError): их не ловят и не обрабатывают
└── Exception
    ├── IOException, SQLException, ... ← checked: компилятор требует обработать или объявить
    └── RuntimeException       ← unchecked: компилятор ничего не требует
        ├── NullPointerException, IllegalArgumentException, IllegalStateException
        ├── IndexOutOfBoundsException, ClassCastException, ArithmeticException
        └── UnsupportedOperationException, ...
```

- **Checked** это `Exception` и его наследники, **кроме** `RuntimeException`. Компилятор требует: либо поймать (`catch`), либо
  объявить в сигнатуре (`throws`). Иначе ошибка сборки: `unreported exception FileNotFoundException; must be caught or
  declared to be thrown`.
- **Unchecked** это `RuntimeException` и `Error`: обрабатывать не обязательно.
- `catch (Exception e)` ловит и checked, и `RuntimeException`, но **не `Error`**.

В C# **checked-исключений нет**, все исключения «непроверяемые». Это главное концептуальное отличие.

### Когда какое

| Ситуация | Что брать |
|---|---|
| Ошибка программиста: неверный аргумент, `null`, неверное состояние | unchecked (`IllegalArgumentException`, `IllegalStateException`, `NullPointerException`) |
| Ожидаемый сбой окружения, который вызывающий может обработать (файл, сеть) | исторически checked (`IOException`) |
| Бизнес-ошибка в своём приложении (в Spring-проектах) | unchecked: свои наследники `RuntimeException` |

Практика современного Java-кода (Spring, Day 15+): **почти всё unchecked**, checked-исключения оборачивают в unchecked
на границе слоя. Причина: checked-исключения «заражают» сигнатуры всех вызывающих методов и **не работают в лямбдах и
стримах** (функциональные интерфейсы вроде `Function` не объявляют `throws`).

Правило переопределения: метод наследника **не может** объявить более широкое checked-исключение, чем метод родителя.

### Аналоги в C#

| C# | Java |
|---|---|
| `ArgumentNullException` | `NullPointerException` (часто через `Objects.requireNonNull(x, "msg")`) |
| `ArgumentException` | `IllegalArgumentException` |
| `InvalidOperationException` | `IllegalStateException` |
| `NotSupportedException`, `NotImplementedException` | `UnsupportedOperationException` |
| `IndexOutOfRangeException` | `ArrayIndexOutOfBoundsException` / `IndexOutOfBoundsException` |
| `InvalidCastException` | `ClassCastException` |
| `DivideByZeroException` | `ArithmeticException` (только для целых; у `double` будет `Infinity`) |
| `KeyNotFoundException` | нет: `Map.get` возвращает `null`; `Optional.get()` и итераторы бросают `NoSuchElementException` |
| `IOException`, `FileNotFoundException` | `IOException`, `FileNotFoundException` (**checked**) |

## `throw` и `throws`

- `throw new X(...)`: оператор, **бросает** исключение (в теле метода);
- `throws X`: часть **сигнатуры** метода, объявляет, что метод может бросить checked-исключение.

```java
void read() throws IOException {          // объявили
    throw new IOException("нет файла");   // бросили
}
```

## `try` / `catch` / `finally`

```java
try {
    ...
} catch (FileNotFoundException e) {       // частное
    ...
} catch (IOException e) {                 // общее: обязательно ПОСЛЕ частного
    ...
} catch (IllegalArgumentException | IllegalStateException e) {   // multi-catch: типы не должны быть родственными
    ...
} finally {
    ...                                   // выполняется всегда (почти)
}
```

- Если сначала поймать общее (`Exception`), а за ним частное (`RuntimeException`), компилятор откажет: `exception
  RuntimeException has already been caught`.
- В multi-catch нельзя сочетать класс и его потомка, а `e` неявно `final`.
- `throw e;` внутри `catch` **сохраняет исходный stack trace** (он заполняется при создании, а не при броске). Проверено:
  `e == orig`. В C# это отдельная ловушка (`throw;` против `throw ex;`), в Java её нет.

### Ловушки `finally` (проверено запуском)

```java
static int f() { try { throw new RuntimeException("lost"); } finally { return 42; } }
f();   // вернёт 42, исключение исчезло без следа

try { throw new IllegalStateException("original"); }
finally { throw new IllegalArgumentException("from finally"); }
// наружу вылетит только IllegalArgumentException, исходное потеряно
```

- **`return` в `finally`** отменяет и возврат из `try`, и брошенное исключение. Не пишите так.
- **Исключение в `finally`** подменяет исходное. Поэтому закрытие ресурсов вручную в `finally` ненадёжно:
  используйте `try-with-resources`.
- В C# `return` из `finally` запрещён компилятором (CS0157), в Java он компилируется (с предупреждением IDE).

Три похожих случая, которые легко перепутать:

| Случай | Что вылетит наружу |
|---|---|
| `try-with-resources`, тело не бросило, `close()` бросил `IOException` | `IOException` (главное: исходного исключения не было) |
| `try-with-resources`, тело бросило, `close()` тоже | исключение тела; ошибка `close()` в `getSuppressed()`, **ничего не теряется** |
| ручной `try { throw A } finally { throw B }` | только `B`; `A` исчезает **без следа и без suppressed** |

Именно третий случай делает ручное закрытие ресурсов в `finally` ненадёжным.

## `try-with-resources` и `AutoCloseable`

Ресурс (файл, соединение) объявляется в скобках `try`, и закрывается автоматически:

```java
try (Res a = new Res("A"); Res b = new Res("B")) {
    ...
}   // сначала close() у B, потом у A (в обратном порядке открытия)
```

Класс должен реализовывать `AutoCloseable` (метод `close()`; для потоков ввода-вывода есть `Closeable`). Это аналог
`IDisposable` и оператора `using` из C#. С Java 9 можно передать уже существующую переменную: `try (res) { ... }`
(она должна быть effectively final).

### Suppressed-исключения

Что если исключение бросает и тело, и `close()`? Проверено запуском:

```
open A / open B  →  тело бросает IllegalStateException("body")  →  close B (бросает), close A (бросает)

primary    = IllegalStateException: body
suppressed = [IOException: close-fail B, IOException: close-fail A]
```

- **Главным** остаётся исключение из тела; исключения из `close()` присоединяются к нему как **suppressed**
  (подавленные) и доступны через `e.getSuppressed()`.
- Если тело отработало без ошибки, а `close()` бросил, то это исключение и будет главным (suppressed пуст).
- Сравнение с C#: в `using` исключение из `Dispose()` **заменяет** исходное, и первая ошибка теряется. Java сохраняет обе.

## Цепочки (`cause`)

Когда одно исключение вызвано другим, нижнее передают как **причину**:

```java
try {
    Integer.parseInt("x");
} catch (NumberFormatException e) {
    throw new AppException("config invalid", e);     // второй аргумент: cause
}
// e.getCause() вернёт NumberFormatException; в stack trace будет секция "Caused by: ..."
```

Правила:

- **всегда передавайте `cause`**: без него вы теряете настоящую причину и место ошибки;
- конструкторы своих исключений делайте двух видов: `(String message)` и `(String message, Throwable cause)`;
- в C# то же называется `InnerException`.

Оборачивание checked в unchecked (для лямбд и границ слоёв):

```java
try { Files.readString(path); }
catch (IOException e) { throw new UncheckedIOException(e); }       // готовая обёртка в JDK
// или своё: throw new AppException("не прочитали " + path, e);
```

## Собственные исключения

```java
public class AppException extends RuntimeException {                   // unchecked, чаще всего так
    public AppException(String message) { super(message); }
    public AppException(String message, Throwable cause) { super(message, cause); }
}
public class NotFoundException extends AppException { ... }           // иерархия по смыслу
public class ValidationException extends AppException { ... }
```

- Название заканчивается на `Exception`.
- Одна базовая `AppException`, дальше наследники по смыслу: удобно ловить «всё своё» одним `catch`, а частное отдельно.
- Если нужно передать данные (код ошибки, имя поля), добавьте поля и геттеры.

## Как не надо: глотание исключений

- **Пустой `catch`**: ошибка исчезла, программа продолжает в неизвестном состоянии. Минимум: залогировать и/или пробросить.
- **`catch (Exception e)` / `catch (Throwable e)`** без причины ловит всё подряд, в том числе то, что обрабатывать не надо.
- **`e.printStackTrace()`**: пишет в stderr в обход логов. Используйте логгер.
- **Лог и повторный проброс на каждом уровне**: одна ошибка запишется в лог десять раз. Логируйте **один раз**, на границе (контроллер, обработчик задач).
- **Исключения как управление потоком** (вместо `if`): медленно, потому что на создание исключения тратится время на сбор stack trace.

## Источники

- [Lesson: Exceptions, Java Tutorials](https://docs.oracle.com/javase/tutorial/essential/exceptions/index.html) (обзор, 10 мин)
- [The Catch or Specify Requirement](https://docs.oracle.com/javase/tutorial/essential/exceptions/catch.html) (7 мин)
- [The finally Block](https://docs.oracle.com/javase/tutorial/essential/exceptions/finally.html) (5 мин)
- [The try-with-resources Statement](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html) (10 мин)
- [Unchecked Exceptions: The Controversy](https://docs.oracle.com/javase/tutorial/essential/exceptions/runtime.html) (5 мин)
- [Creating Exception Classes](https://docs.oracle.com/javase/tutorial/essential/exceptions/creating.html) (5 мин)
- [Chained Exceptions](https://docs.oracle.com/javase/tutorial/essential/exceptions/chained.html) (4 мин)
- [`Throwable`, Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Throwable.html) (справочно: `getCause`, `getSuppressed`)
- [JLS, глава 11: Exceptions](https://docs.oracle.com/javase/specs/jls/se25/html/jls-11.html) (справочно)
- [Exception handling, C# (Microsoft Learn)](https://learn.microsoft.com/en-us/dotnet/csharp/fundamentals/exceptions/) (10 мин, для сравнения)
- [`using` statement, C# (Microsoft Learn)](https://learn.microsoft.com/en-us/dotnet/csharp/language-reference/statements/using) (5 мин, для сравнения)
- [Best practices for exceptions, .NET (Microsoft Learn)](https://learn.microsoft.com/en-us/dotnet/standard/exceptions/best-practices-for-exceptions) (10 мин, для сравнения)
