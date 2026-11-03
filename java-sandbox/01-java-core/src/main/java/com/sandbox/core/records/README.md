# Records, sealed-иерархии и pattern matching

Код: [RecordsDemo.java](RecordsDemo.java), [PatternMatchingDemo.java](PatternMatchingDemo.java).

## Record: неизменяемый класс-данные

`record` объявляет класс из набора **компонентов**; компилятор сам создаёт:

- приватные `final`-поля и **канонический конструктор** со всеми компонентами;
- **методы доступа без префикса `get`**: `dev.name()`, а не `dev.getName()`;
- `equals`, `hashCode` (по значениям всех компонентов) и `toString` (`P[name=Alex, age=7]`).

```java
record Point(int x, int y) {}          // всё перечисленное сгенерировано
```

`OldSchoolDeveloper` из `RecordsDemo` показывает ту же работу руками: поля, конструктор, геттеры, `equals` через
`instanceof` и `Objects.equals`, `hashCode` через `Objects.hash`, `toString`. Запись на `record` короче в разы.

Ограничения:

- record **неявно `final`** и не может наследовать другой класс (наследует `java.lang.Record`), но может реализовывать интерфейсы;
- нельзя добавлять поля экземпляра, кроме компонентов (статические можно);
- immutability **поверхностная**: если компонент это `List`, то список менять можно. Для настоящей неизменяемости
  копируйте его в конструкторе через `List.copyOf`.

Сравнение с C#: это аналог `record` (`record class`) из C# 9, но компоненты это **методы** (`p.x()`), а не свойства (`p.X`).
Оператора `with` в Java нет: копию с изменённым полем пишут методом (`withExpYears`) или генерируют через Lombok
(`toBuilder`).

### Компактный конструктор

```java
public record ModernDeveloper(String name, int expYears) {
    public ModernDeveloper {                      // без списка параметров
        if (expYears < 0) throw new IllegalArgumentException("...");
        if (name == null || name.isBlank()) name = "Unknown";   // можно изменить параметр
    }                                              // поля присваиваются ПОСЛЕ тела автоматически
}
```

Тело выполняется **до** присваивания полей, внутри можно проверять и нормализовать *параметры*. Присваивать `this.name`
в компактном конструкторе нельзя. Конструктор вызывается при любом создании (в том числе из Lombok-билдера), поэтому
проверка здесь работает всегда.

Обычные методы добавляются как в классе: `isSenior()` в `ModernDeveloper`. Он не создаёт поле, потому что
`isSenior` не компонент.

## Lombok: `@Builder(toBuilder = true)`

Lombok это процессор аннотаций: на этапе компиляции он **генерирует код**. Зависимость поэтому подключена двумя строками:
`compileOnly` (нужна компилятору, в jar не попадает) и `annotationProcessor` (запускает генерацию).

```java
@Builder(toBuilder = true)
public record ModernDeveloper(String name, int expYears) {}

var d2 = dev1.toBuilder().expYears(10).build();   // копия с изменённым полем
var d3 = ModernDeveloper.builder().name("Bob").expYears(3).build();
```

`@Builder` удобен, когда в record много полей (параметры по именам вместо длинного конструктора). Для двух полей
достаточно ручного `withExpYears`. Версия Lombok должна поддерживать вашу версию JDK (для JDK 25 нужна свежая
1.18.4x: старые падают с `ExceptionInInitializerError` при компиляции).

## Bean Validation на компонентах

```java
public record ModernDeveloper(
    @NotBlank(message = "Имя обязательно") @Size(min = 2, max = 50) String name,
    @Min(0) @Max(70) int expYears) {}
```

Аннотации `jakarta.validation.constraints.*` это **только метаданные**. Само по себе ничего не проверяется. Нужны:

1. реализация спецификации (в зависимостях сейчас только `jakarta.validation-api`, то есть API; реализация это
   Hibernate Validator);
2. запуск проверки: `validator.validate(obj)` вручную или `@Valid` на параметре метода контроллера в Spring (День 17).

Для record аннотация на компоненте распространяется на поле, метод доступа и параметр конструктора в зависимости от
допустимых целей (`@Target`) самой аннотации.

Обратите внимание на конфликт в `ModernDeveloper`: компактный конструктор **молча заменяет пустое имя на `"Unknown"`**,
поэтому `@NotBlank` на `name` не сработает никогда. Выбирайте одно из двух: проверять и падать, или нормализовать.

Аналог в C#: `System.ComponentModel.DataAnnotations` (`[Required]`, `[StringLength]`, `[Range]`) с тем же условием,
что кто-то должен запустить проверку (в ASP.NET Core это модель привязки, в Spring это `@Valid`).

## Комментарии `///`

В `RecordsDemo` документация написана через `///`: это Markdown-комментарии javadoc (Java 23+, JEP 467). Вместо
HTML-тегов можно писать Markdown (`**жирный**`, `` `код` ``, списки). Это аналог XML-документации `///` из C#, но с
Markdown вместо XML.

## Sealed-иерархии

```java
public sealed interface ApiResponse permits Success, ClientError, ServerError {}

public record Success(String body, int statusCode) implements ApiResponse {}
public record ClientError(String message, int errorCode) implements ApiResponse {}
public record ServerError(Throwable exception) implements ApiResponse {}
```

`sealed` ограничивает, **кто может реализовать** интерфейс или наследовать класс. Допустимые наследники перечисляются в
`permits` (если они в том же файле, список можно опустить). Каждый из них обязан быть `final`, `sealed` или `non-sealed`;
record неявно `final`.

Главная польза: **компилятор знает все варианты**, поэтому проверяет полноту `switch`. Добавите четвёртый вариант в
`permits`: каждый `switch` без `default` перестанет компилироваться и укажет, где обработать новый случай. В C#
закрытых иерархий нет (на момент написания), поэтому там такой проверки нет.

## Pattern matching: `switch` как выражение

```java
String text = switch (response) {
    case Success(String body, int code)             -> "Успех [" + code + "]: " + body;
    case ClientError(String msg, int code) when code == 404 -> "Не найдено: " + msg;
    case ClientError(String msg, int code)          -> "Ошибка клиента (" + code + "): " + msg;
    case ServerError(Throwable ex)                  -> "Фатальная ошибка: " + ex.getMessage();
};   // default не нужен: sealed-иерархия покрыта целиком
```

Что здесь происходит:

- **`switch` возвращает значение** и использует `->`: без `break` и без проваливания (в отличие от старого
  `switch` с `:`);
- **record pattern** `Success(String body, int code)` проверяет тип *и* сразу достаёт компоненты (деконструкция).
  Типы можно заменить на `var`: `Success(var body, var code)`; шаблоны можно вкладывать друг в друга;
- **guard `when`** добавляет условие к шаблону;
- **порядок веток важен.** Более частный шаблон (`when code == 404`) нужно ставить **раньше** общего. Если поставить
  общий первым, компилятор откажет: *this case label is dominated by a preceding case label*;
- если `switch` по шаблонам получит `null`, будет `NullPointerException`, если не написать `case null`;
- `ex.getMessage()` может вернуть `null`: у исключения не обязательно есть сообщение.

Сравнение с C#: синтаксис очень близок (`switch` выражение, позиционные шаблоны, `when`). Главное отличие — sealed и
проверка полноты.

### `instanceof` с шаблоном

```java
Object raw = "Привет, мир";
if (raw instanceof String text && !text.isEmpty()) {
    System.out.println(text.toUpperCase());   // text уже String, без приведения
}
```

Переменная `text` видна там, где компилятор уверен, что проверка прошла (после `&&` и внутри `if`). Аналог в C#:
`if (raw is string text && text.Length > 0)`.

## Источники

- [Records, dev.java](https://dev.java/learn/records/) (15 мин)
- [JEP 395: Records](https://openjdk.org/jeps/395) (15 мин)
- [JEP 409: Sealed Classes](https://openjdk.org/jeps/409) (10 мин)
- [JEP 394: Pattern Matching for instanceof](https://openjdk.org/jeps/394) (7 мин)
- [JEP 440: Record Patterns](https://openjdk.org/jeps/440) (10 мин)
- [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441) (15 мин)
- [JEP 467: Markdown Documentation Comments](https://openjdk.org/jeps/467) (5 мин)
- [Lombok: `@Builder`](https://projectlombok.org/features/Builder) (7 мин)
- [Jakarta Bean Validation 3.1](https://jakarta.ee/specifications/bean-validation/3.1/) (справочно)
- [Records, C# (Microsoft Learn)](https://learn.microsoft.com/en-us/dotnet/csharp/language-reference/builtin-types/record) (10 мин, для сравнения)
- [Pattern matching, C# (Microsoft Learn)](https://learn.microsoft.com/en-us/dotnet/csharp/fundamentals/functional/pattern-matching) (10 мин, для сравнения)
