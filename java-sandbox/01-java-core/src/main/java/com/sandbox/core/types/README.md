# Типы, память и равенство

Код: [TypesDemo.java](TypesDemo.java), [EqualsDemo.java](EqualsDemo.java), тест [EqualityTest.java](../../../../../../test/java/com/sandbox/core/types/EqualityTest.java).

## Примитивы и обёртки

В Java два мира типов: **примитивы** (`int`, `long`, `double`, `boolean`, `char`, `byte`, `short`, `float`) и **ссылочные типы**.
У каждого примитива есть класс-обёртка (`Integer`, `Long`, ...). Обёртка нужна там, где требуется объект: в дженериках
(`List<Integer>`, но не `List<int>`), там, где значение может быть `null`, и для вызова методов.

| Примитив | Обёртка | Размер | Диапазон | Значение поля по умолчанию |
|---|---|---|---|---|
| `byte` | `Byte` | 8 бит | -128 … 127 | `0` |
| `short` | `Short` | 16 бит | -32 768 … 32 767 | `0` |
| `int` | `Integer` | 32 бита | ±2,1 млрд | `0` |
| `long` | `Long` | 64 бита | ±9,2·10¹⁸ | `0L` |
| `float` | `Float` | 32 бита | ±3,4·10³⁸ | `0.0f` |
| `double` | `Double` | 64 бита | ±1,8·10³⁰⁸ | `0.0d` |
| `char` | `Character` | 16 бит | 0 … 65 535 (беззнаковый) | `'\u0000'` |
| `boolean` | `Boolean` | не определён спецификацией | `true` / `false` | `false` |

Размер `boolean` в спецификации JVM не задан: логически это 1 бит, в массиве обычно 1 байт, а локальная переменная
занимает целый слот (32 бита). Поэтому комментарий «1 бит» в коде стоит читать как «логически».

Значение по умолчанию получают только **поля** объекта. **Локальная переменная** без присваивания не компилируется
(как и в C#). Поле-обёртка по умолчанию `null`, поле-примитив `0`.

Сравнение с C#: нет `struct` и пользовательских значимых типов (пока), нет беззнаковых `uint`/`ulong`, нет `decimal`
(вместо него `BigDecimal`), нет `int?` (`Nullable<T>`): допускающий `null` числовой тип это просто `Integer`.

### Неявные преобразования в присваивании

```java
Byte b = 0;        // компилируется: константа int помещается в byte, потом упаковка
Character c = 0;   // то же для char
Long l = 0;        // ОШИБКА: int cannot be converted to Long
Long l2 = 0L;      // правильно
Float f = 0.0;     // ОШИБКА: 0.0 это double; нужно 0.0f
```

Правило: упаковка и расширение (`int → long`) вместе не работают. Исключение составляют константы, влезающие в `byte`,
`short`, `char`.

### `Float.MIN_VALUE` и `Double.MIN_VALUE`

Для целых типов `MIN_VALUE` это наименьшее число. Для `float` и `double` это наименьшее **положительное** число
(`1.4E-45` и `4.9E-324`). Самое отрицательное: `-Float.MAX_VALUE`. Причина: знак у них хранится отдельным битом,
диапазон симметричен. В C# `float.MinValue` это `-3.4028235E+38`, а `1.4E-45` там называется `float.Epsilon`.

`Float.MIN_NORMAL` (`1.17549435E-38`) это наименьшее *нормализованное* число; всё, что меньше его по модулю, хранится как
«денормализованное» с потерей точности.

### Беззнаковая арифметика

Беззнаковых типов нет, но есть методы-помощники: `Integer.toUnsignedString(x)`, `Integer.divideUnsigned(a, b)`,
`Integer.parseUnsignedInt(s)`. Выражение `2147483647 * 2` переполняется до `-2`, а `toUnsignedString(-2)` печатает
`4294967294`: это те же 32 бита, прочитанные как беззнаковые.

## Autoboxing и unboxing

Компилятор подставляет преобразования сам:

```java
Integer a = 5;       // Integer.valueOf(5)
int b = a;           // a.intValue()
```

Две ловушки.

**1. Распаковка `null` бросает `NullPointerException`:**

```java
Integer boxed = null;
int x = boxed;                      // NPE

Map<Integer, Integer> m = new HashMap<>();
Integer ok = m.get(2);              // null, исключения нет
int v = m.get(2);                   // NPE: get вернул null, а int null не хранит
```

Это самый частый вариант в реальном коде. `HashMap.get(null)` сам по себе исключения не бросает: `HashMap` допускает
`null`-ключ (в отличие от `TreeMap`, `ConcurrentHashMap` и `Map.of`).

**2. Тернарный оператор.** Тип выражения `c ? x : y` определяется по JLS §15.25:

```java
boolean flag = false;
Integer boxed = null;

Integer a = flag ? 1 : null;      // тип выражения Integer, никакого NPE, a == null
Integer b = flag ? 1 : boxed;     // оба операнда числовые → тип int → boxed распаковывается → NPE
```

Если один операнд `int`, а другой `Integer`, то результат `int`, и `null` распаковывается.

## Integer Cache и сравнение

`==` на ссылочных типах сравнивает **ссылки**, `equals` сравнивает значения. На примитивах `==` сравнивает значения.

При упаковке вызывается `Integer.valueOf(x)`, и он для значений от -128 до 127 возвращает заранее созданные объекты из
кэша, а для остальных создаёт новый:

| Обёртка | Что кэшируется |
|---|---|
| `Integer`, `Short`, `Byte`, `Long` | -128 … 127 |
| `Character` | 0 … 127 |
| `Boolean` | оба значения |
| `Float`, `Double` | не кэшируются |

Спецификация языка (§5.1.7) гарантирует кэш для `boolean`, `byte`, `char <= 127`, `short` и `int` в диапазоне
-128…127. Кэш `Long` это поведение HotSpot, но на практике работает так же. Верхнюю границу кэша `Integer` можно
поднять флагом JVM `-XX:AutoBoxCacheMax=...`.

```java
Integer a = 127, b = 127;   a == b            // true   (один объект из кэша)
Integer c = 128, d = 128;   c == d            // false  (два объекта)
                            c.equals(d)       // true
Long l = 127L;              l.equals(127)     // false: 127 это Integer, типы разные
Integer z = 127; long y = 127L;  z == y      // true: z распаковывается, сравнение чисел
```

Правило для кода: ссылочные типы сравнивайте через `equals`, а при риске `null` через `Objects.equals(a, b)`
(возвращает `false`, если ровно один из аргументов `null`, и `true`, если оба).

Сравнение с C#: там `==` на `int`, `string`, `record` сравнивает значения, а ссылки сравнивает `ReferenceEquals`. В Java
эквивалента нет: `==` всегда сравнивает ссылки для объектов.

## Переполнение целых

Целочисленное переполнение в Java **молча заворачивается**: `Integer.MAX_VALUE + 1 == Integer.MIN_VALUE`. Это справедливо
даже для константных выражений. В C# константное переполнение это ошибка компиляции, а в рантайме есть `checked`.
Аналог `checked` в Java: методы `Math.addExact`, `subtractExact`, `multiplyExact`, `toIntExact`, которые бросают
`ArithmeticException`.

## Передача аргументов: всегда по значению

В методе доступна **копия** того, что передали. Для примитива это копия числа, для объекта копия **ссылки**. Отсюда
четыре случая из `TypesDemo`:

| Метод | Что делает | Виден снаружи? |
|---|---|---|
| `patchNumber(Integer n) { n++; }` | переприсваивает локальную копию (`n = Integer.valueOf(n + 1)`) | нет |
| `patchString(String s) { s += "x"; }` | создаёт новую строку, переприсваивает копию ссылки | нет |
| `patchList(List l) { l.add(55); }` | меняет *объект*, на который указывает копия ссылки | **да** |
| `reassignList(List l) { l = new ArrayList<>(); }` | копия ссылки теперь указывает на другой объект | нет |

Различает случаи один вопрос: метод **меняет объект** или **переприсваивает параметр**. Первое видно снаружи, второе нет.
Поэтому нельзя написать `swap(int a, int b)`: ключевых слов `ref` и `out`, как в C#, нет. Чтобы вернуть несколько
значений, возвращают `record` или массив.

## Тесты на JUnit 5

В [EqualityTest.java](../../../../../../test/java/com/sandbox/core/types/EqualityTest.java) использованы:

- `@Test`: метод-тест (в JUnit 5 он может быть package-private);
- `assertEquals(expected, actual)`: **сначала ожидаемое, потом фактическое**;
- `assertSame` / `assertNotSame`: проверяют ссылки (`==`), точнее выражают намерение, чем `assertTrue(a == b)`;
- `assertThrows(ArithmeticException.class, () -> Math.addExact(Integer.MAX_VALUE, 1))`: проверка исключения; лямбда
  `Executable` не возвращает значение.

Запуск: `./gradlew :01-java-core:test` (с `JAVA_HOME` на JDK 25, если `java` нет в `PATH`).

## Источники

- [Primitive Data Types, Java Tutorials](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html) (10 мин)
- [Autoboxing and Unboxing, Java Tutorials](https://docs.oracle.com/javase/tutorial/java/data/autoboxing.html) (8 мин)
- [Passing Information to a Method, Java Tutorials](https://docs.oracle.com/javase/tutorial/java/javaOO/arguments.html) (8 мин, раздел про ссылочные аргументы)
- [JLS §5.1.7: Boxing Conversion](https://docs.oracle.com/javase/specs/jls/se25/html/jls-5.html#jls-5.1.7) (5 мин, гарантии кэша)
- [JLS §15.25: Conditional Operator](https://docs.oracle.com/javase/specs/jls/se25/html/jls-15.html#jls-15.25) (10 мин, таблица типов тернарного оператора)
- [Класс `Integer`, Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Integer.html) (справочно: `valueOf`, `toUnsignedString`, `divideUnsigned`)
- [Класс `Float`, Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Float.html) (справочно: `MIN_VALUE`, `MIN_NORMAL`)
- [Класс `Math`, Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Math.html) (справочно: `addExact` и другие)
