# Дженерики и стирание типов

Код этого пакета пишете вы по заданиям Дня 6 (`Pair<A, B>`, `Result<T>`, `copy`, демонстрация стирания). Здесь теория,
сравнение с C# и источники. Все примеры проверены запуском на JDK 25.

## Зачем нужны дженерики

Они переносят проверку типов на этап компиляции и убирают ручные приведения:

```java
List names = new ArrayList();          // «сырой» тип: можно положить что угодно
names.add(1);
String s = (String) names.get(0);      // ClassCastException в рантайме

List<String> names = new ArrayList<>();
names.add(1);                          // ошибка компиляции
String s = names.get(0);               // без приведения
```

## Синтаксис

```java
class Box<T> { private T value; ... }                       // обобщённый класс
record Pair<A, B>(A first, B second) {}                      // обобщённый record
interface Repository<T, ID> { T find(ID id); }               // обобщённый интерфейс

static <T> T firstOf(List<T> list) { return list.get(0); }   // обобщённый метод: <T> перед возвращаемым типом
static <T extends Comparable<T>> T max(List<T> list) { ... } // ограничение сверху: T должен быть Comparable<T>
<T extends Number & Comparable<T>> ...                       // несколько границ через &
```

Принятые имена параметров: `T` (type), `E` (element), `K`/`V` (key/value), `R` (result). Диамант `<>` избавляет от
повторения типов: `new HashMap<>()`.

## Стирание типов (type erasure)

В Java дженерики существуют **только для компилятора**. После проверки типов компилятор:

1. **стирает** параметры: `T` заменяется на `Object` (или на границу, например `Comparable`);
2. **вставляет приведения** там, где вы читаете значение;
3. при необходимости создаёт «мостовые» методы, чтобы переопределения продолжали работать.

Поэтому в рантайме **нет `List<String>` и `List<Integer>`, есть только `List`**:

```java
new ArrayList<String>().getClass() == new ArrayList<Integer>().getClass()   // true, оба java.util.ArrayList
```

### Чего нельзя из-за стирания (ошибки компилятора проверены)

| Что | Сообщение компилятора |
|---|---|
| `new T()` | `unexpected type` |
| `new T[1]` | `generic array creation` |
| `o instanceof List<String>` | `Object cannot be safely cast to List<String>` (можно `o instanceof List<?>`) |
| `List<int>` | `unexpected type`: аргументом может быть только ссылочный тип, поэтому `List<Integer>` с упаковкой |
| `static T field;` | `non-static type variable T cannot be referenced from a static context` |
| `void f(List<String>)` и `void f(List<Integer>)` в одном классе | `name clash: ... have the same erasure` |

Если типа нужно знать в рантайме, его передают явно, как значение: `Class<T>` (например `create(Class<T> type)`).
Записи вида `T.class` недопустимы.

### Сравнение с C#

В .NET дженерики **reified**: типы существуют и в рантайме.

| | C# | Java |
|---|---|---|
| `typeof(List<int>) == typeof(List<string>)` | `false`, это разные типы | один класс `ArrayList` |
| `new T()` | можно с ограничением `where T : new()` | нельзя |
| `typeof(T)`, `default(T)` | можно | нельзя (`Class<T>` вручную) |
| `List<int>` | значения хранятся без упаковки | только `List<Integer>`, каждое число это объект |
| Вариантность | на уровне объявления: `IEnumerable<out T>`, `Action<in T>` | на уровне использования: wildcards `? extends` / `? super` |
| Ограничения | `where T : IComparable<T>` | `<T extends Comparable<T>>` |

## Инвариантность и wildcards

`List<Integer>` **не является** `List<Number>`, хотя `Integer` это `Number`:

```java
List<Object> lo = new ArrayList<String>();   // ошибка: ArrayList<String> cannot be converted to List<Object>
```

Причина: иначе можно было бы положить в такой список `Double`. Массивы, наоборот, **ковариантны** (и ломаются в
рантайме):

```java
Object[] arr = new String[1];
arr[0] = 1;       // ArrayStoreException
```

Чтобы писать универсальные методы, используют **wildcards** (`?`):

| Запись | Смысл | Что можно |
|---|---|---|
| `List<?>` | список чего-то неизвестного | читать как `Object`; добавлять нельзя (кроме `null`) |
| `List<? extends Number>` | список `Number` или потомков | **читать** как `Number`; добавлять нельзя |
| `List<? super Integer>` | список `Integer` или предков | **добавлять** `Integer`; читать можно только как `Object` |

### PECS: Producer Extends, Consumer Super

- коллекция, **из которой вы читаете** (производитель), объявляется `? extends T`;
- коллекция, **в которую вы пишете** (потребитель), объявляется `? super T`.

```java
static <T> void copy(List<? super T> dst, List<? extends T> src) {
    for (T t : src) dst.add(t);            // из src читаем (extends), в dst пишем (super)
}

List<Number> nums = new ArrayList<>();
List<Integer> ints = List.of(1, 2, 3);
copy(nums, ints);                          // [1, 2, 3]: Integer кладём в список Number

static double sum(List<? extends Number> nums) {      // принимает List<Integer>, List<Double>, ...
    double s = 0;
    for (Number n : nums) s += n.doubleValue();
    return s;
}
```

Правило выбора: если параметр и читаете, и пишете, используйте точный тип без wildcard (`List<T>`).

## Heap pollution и сырые типы

**Heap pollution** это ситуация, когда переменная типа `List<String>` на самом деле содержит не-строки. Для этого
достаточно сырого типа (`raw type`, `List` без параметров) или небезопасного приведения:

```java
List<String> strings = new ArrayList<>(List.of("a"));
List raw = strings;           // предупреждение unchecked / rawtypes
raw.add(1);                   // компилятор молчит, в списке теперь Integer
String s = strings.get(1);    // ClassCastException: Integer cannot be cast to String
```

Ошибка вылетает **не там, где совершена**, а позже, при чтении: из-за стирания проверки в момент `add` нет, а
компилятор вставил приведение к `String` только на `get`. Поэтому: не используйте сырые типы, не подавляйте предупреждения
`unchecked` без доказательства безопасности. Для методов с обобщённым varargs есть `@SafeVarargs`.

## Обобщённые record и sealed-иерархии

```java
record Pair<A, B>(A first, B second) {
    <C> Pair<A, C> withSecond(C c) { return new Pair<>(first, c); }   // свой параметр у метода
}

sealed interface Result<T> permits Ok, Err {}
record Ok<T>(T value) implements Result<T> {}
record Err<T>(String error) implements Result<T> {}

Result<Integer> r = new Ok<>(42);
String text = switch (r) {
    case Ok<Integer>(var v)  -> "ok " + v;
    case Err<Integer>(var e) -> "err " + e;
};                                          // sealed: default не нужен
```

`Result<T>` это аналог `Either`/`Result` из функциональных языков и распространённая замена исключениям для
ожидаемых ошибок. Связь с прошлыми днями: ограничение `<T extends Comparable<T>>` использует контракт `compareTo`
(День 4), а sealed и record-шаблоны описаны в пакете `records`.

## Типичные ошибки

- `List<Object>` не равен `List<?>`: в первый можно класть любые объекты, но он не принимает `List<String>`; второй
  принимает любой список, но писать в него нельзя.
- Сырые типы (`List`) обходят проверку, и потом всё «ломается» в рантайме в другом месте.
- Нельзя перегружать методы, отличающиеся только параметром дженерика (одинаковое стирание).
- Не создавайте массивы обобщённых типов: `new T[n]`, `new List<String>[n]` запрещены; используйте `List<T>` или
  `(T[]) new Object[n]` с осторожностью.

## Источники

- [Lesson: Generics, Java Tutorials](https://docs.oracle.com/javase/tutorial/java/generics/index.html) (обзор, 5 мин)
- [Generic Types](https://docs.oracle.com/javase/tutorial/java/generics/types.html) (7 мин)
- [Generic Methods](https://docs.oracle.com/javase/tutorial/java/generics/methods.html) (5 мин)
- [Bounded Type Parameters](https://docs.oracle.com/javase/tutorial/java/generics/bounded.html) (7 мин)
- [Generics, Inheritance, and Subtypes](https://docs.oracle.com/javase/tutorial/java/generics/inheritance.html) (5 мин)
- [Wildcards](https://docs.oracle.com/javase/tutorial/java/generics/wildcards.html) (3 мин)
- [Upper Bounded Wildcards](https://docs.oracle.com/javase/tutorial/java/generics/upperBounded.html) (5 мин)
- [Lower Bounded Wildcards](https://docs.oracle.com/javase/tutorial/java/generics/lowerBounded.html) (4 мин)
- [Wildcard Guidelines](https://docs.oracle.com/javase/tutorial/java/generics/wildcardGuidelines.html) (5 мин, это и есть PECS)
- [Type Erasure](https://docs.oracle.com/javase/tutorial/java/generics/erasure.html) (10 мин)
- [Restrictions on Generics](https://docs.oracle.com/javase/tutorial/java/generics/restrictions.html) (10 мин)
- [Non-Reifiable Types, Heap Pollution](https://docs.oracle.com/javase/tutorial/java/generics/nonReifiableVarargsType.html) (7 мин)
- [Generics, C# (Microsoft Learn)](https://learn.microsoft.com/en-us/dotnet/csharp/fundamentals/types/generics) (10 мин, для сравнения)
- [Covariance and contravariance, .NET (Microsoft Learn)](https://learn.microsoft.com/en-us/dotnet/standard/generics/covariance-and-contravariance) (10 мин, для сравнения)
