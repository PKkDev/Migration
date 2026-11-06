# Контракт `equals` / `hashCode` / `compareTo`

Код этого пакета пишете вы по заданиям Дня 4. Здесь теория, сравнение с C# и источники.

## Идентичность и равенство

- **Идентичность** (`==`): две переменные указывают на один и тот же объект.
- **Равенство** (`equals`): объекты считаются «одинаковыми по смыслу».

По умолчанию `Object.equals` **это и есть `==`**, а `Object.hashCode` возвращает идентификационный хеш объекта.
`System.identityHashCode(o)` всегда возвращает его же, даже если у класса переопределён `hashCode`. Проверено запуском:
для `new Object()` `hashCode()` и `identityHashCode()` совпадают, для `String` нет (у строки `hashCode` считается по
содержимому).

Если вам нужно сравнивать по смыслу (две записи с одинаковым `id` это одна запись), переопределяйте **оба** метода,
`equals` и `hashCode`. В C# так же: `Equals` и `GetHashCode`, плюс (опционально) `IEquatable<T>` и операторы `==`/`!=`.
Принципиальное отличие: в C# `==` можно перегрузить, в Java нет.

## Контракт `equals`

Для любых ненулевых `x`, `y`, `z`:

1. **Рефлексивность:** `x.equals(x)` истинно.
2. **Симметричность:** `x.equals(y)` тогда и только тогда, когда `y.equals(x)`.
3. **Транзитивность:** из `x.equals(y)` и `y.equals(z)` следует `x.equals(z)`.
4. **Согласованность:** повторные вызовы дают тот же результат, пока объекты не менялись.
5. `x.equals(null)` всегда `false` (и никогда не бросает исключение).

Шаблон (Java 16+), как писать `equals` и `hashCode` для неизменяемого класса:

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Point p)) return false;     // и null отсекает, и тип проверяет
    return x == p.x && y == p.y;
}

@Override
public int hashCode() {
    return Objects.hash(x, y);
}
```

Как сравнивать поля: примитивы через `==`, ссылки через `Objects.equals(a, b)` (безопасно к `null`), `double` через
`Double.compare(a, b) == 0`, массивы через `Arrays.equals` (у массива `equals` это идентичность: для `int[]`
`a.equals(b)` даёт `false` даже при одинаковом содержимом).

`Objects.hash(...)` удобен, но создаёт массив и упаковывает примитивы. Для горячих мест хеш пишут руками
(`31 * x + y`).

### Наследование ломает симметричность

Проверено запуском:

```java
class Pt   { equals: o instanceof Pt p && x == p.x && y == p.y }
class CPt extends Pt { equals: o instanceof CPt p && super.equals(o) && color.equals(p.color) }

Pt p = new Pt(1, 2);  CPt c = new CPt(1, 2, "red");
p.equals(c)   // true   (c это Pt с теми же координатами)
c.equals(p)   // false  (p не CPt)   ← нарушена симметричность
```

Если сравнивать по `getClass()` вместо `instanceof`, симметричность сохраняется, но нарушается принцип подстановки
(наследник уже не «равен» родителю). Универсального решения нет, поэтому **не наследуйте классы с `equals` и добавляйте
поля, влияющие на равенство**. Предпочитайте композицию, `final`-классы и `record`.

## Контракт `hashCode`

1. Пока объект не менялся, `hashCode()` возвращает одно и то же число в рамках запуска программы.
2. **Если `x.equals(y)`, то `x.hashCode() == y.hashCode()`.** Это главное правило.
3. Обратное необязательно: **разные объекты могут иметь одинаковый хеш** (коллизия). Классический пример:
   `"Aa".hashCode() == "BB".hashCode() == 2112`. Но чем меньше коллизий, тем быстрее работают `HashMap` и `HashSet`.

Хеш-коллекции (`HashMap`, `HashSet`) работают в два шага: по `hashCode()` выбирают корзину, потом внутри корзины ищут
элемент через `equals`. Поэтому оба метода должны быть согласованы.

### Что ломается при нарушении (проверено запуском)

| Ситуация | Результат |
|---|---|
| переопределён `equals`, `hashCode` нет | два «равных» объекта в `HashSet` дают `size() == 2` |
| переопределён `hashCode`, `equals` нет | тоже `size() == 2` (хеш одинаковый, но `equals` это `==`) |
| переопределены оба | `size() == 1` |

Первая строка зависит от идентификационных хешей, поэтому формально это «обычно», но на практике почти всегда.

## Изменяемые поля в ключе `HashMap`

Если поле, участвующее в `hashCode`, изменить **после** `put`, запись теряется:

```java
Ok key = new Ok("a");           // hashCode считается по полю n
map.put(key, "v");
key.n = "b";                    // хеш изменился

map.get(key);                   // null: ищем в корзине для "b", а запись лежит в корзине для "a"
map.containsKey(key);           // false
map.get(new Ok("a"));           // null: корзина верная, но equals к изменённому ключу уже ложно
map.size();                     // 1: запись всё ещё занимает память
map.values();                   // [v]: через итерацию она видна, но найти её по ключу нельзя
```

Запись «потеряна»: она остаётся в мапе (утечка памяти), но достать её по ключу невозможно. Документация `Map`
прямо предупреждает об этом. Правила:

- ключи должны быть **неизменяемыми**: `String`, `Integer`, `UUID`, `record` из неизменяемых полей;
- поля для `equals`/`hashCode` делайте `final`;
- если ключ всё-таки меняли, удалите и вставьте запись заново.

В C# та же проблема с `Dictionary<TKey,TValue>` и изменяемым ключом.

### Почему `get(new Key("a"))` тоже `null`

Казалось бы, `new Key("a")` и есть исходный ключ. Но `HashMap` ищет в два шага (устройство подробно: [collections/README.md](../collections/README.md),
раздел «Как работает `HashMap`»):

1. **Корзина по хешу.** Запись при `put` легла в корзину, соответствующую хешу `"a"`, и вместе с ней в узле
   сохранился **хеш `"a"`**. Хеш `new Key("a")` тот же, корзина найдена.
2. **Сравнение через `equals`.** В узле лежит ссылка на тот же объект `key`, у которого теперь `id = "b"`.
   `new Key("a").equals(key)` даёт `"a"` против `"b"`, то есть `false`: запись отвергнута.

| Что передаём в `get` | Что происходит | Результат |
|---|---|---|
| `key` (теперь `id = "b"`) | хеш `"b"` указывает на другую корзину | `null` |
| `new Key("a")` | корзина верная, но `equals` с изменённым ключом ложно | `null` |
| `new Key("b")` | хеш `"b"`: другая корзина | `null` |

Найти запись **не может никакой ключ**: чтобы попасть в её корзину, нужен хеш `"a"`, а чтобы пройти `equals`, нужен
`id = "b"`, но у равных объектов хеши равны. Остаётся только итерация (поэтому `values()` запись показывает).
Проверка: верните `key.id = "a"`, и `get(key)` и `get(new Key("a"))` снова возвращают значение.

Как поймать это тестом: после `put` измените ключ и проверьте `assertNull(map.get(key))`,
`assertFalse(map.containsKey(key))`, `assertEquals(1, map.size())` (так сделан тест `missedKey` в `EqualityTest`).

## `record` и Lombok

`record` (см. пакет `records`) сам генерирует `equals` и `hashCode` по всем компонентам:
`new Person("A", 1).equals(new Person("A", 1))` истинно, хеши совпадают. Для обычных классов то же делает Lombok
`@EqualsAndHashCode`. Для сущностей JPA (День 22) с этим нужна осторожность: изменяемые поля, ленивые прокси и `id`,
присваиваемый базой после сохранения, часто делают `equals` и `hashCode` неверными.

## `Comparable` и `Comparator`

Две вещи про упорядочение:

| | `Comparable<T>` | `Comparator<T>` |
|---|---|---|
| Что это | «естественный» порядок класса | внешний порядок, передаётся отдельно |
| Где | метод `compareTo` внутри класса | отдельный объект/лямбда |
| Сколько порядков | один | сколько угодно |
| Аналог в C# | `IComparable<T>` | `IComparer<T>` / `Comparison<T>` |

Контракт `compareTo`: возвращает отрицательное число, ноль или положительное; `sgn(x.compareTo(y)) == -sgn(y.compareTo(x))`;
транзитивность; **желательно согласован с `equals`**: `compareTo == 0` тогда и только тогда, когда `equals`.

Пример, где они не согласованы (проверено): `BigDecimal("2.0")` и `BigDecimal("2.00")`:
- `equals` ложно (разный `scale`), поэтому `HashSet` из них даёт `size() == 2`;
- `compareTo == 0`, поэтому `TreeSet` из них даёт `size() == 1`.

`TreeSet` и `TreeMap` определяют «одинаковость» по `compareTo` (или `Comparator`), а не по `equals`. Добавление в
`TreeSet` объекта без `Comparable` и без компаратора бросает `ClassCastException`.

### Ловушка вычитания

Никогда не сравнивайте числа вычитанием: `a - b` переполняется. `Integer.MAX_VALUE - (-1)` даёт отрицательное
`-2147483648`, а должно быть положительным. Используйте `Integer.compare(a, b)`, `Double.compare(a, b)`.

### `Comparator.comparing` и цепочки

```java
list.sort(Comparator.comparing(Person::age).thenComparing(Person::name));
// Carl(25), Alex(30), Bob(30): по возрасту, при равенстве по имени

list.sort(Comparator.comparing(Person::age).reversed()
                    .thenComparing(Person::name, Comparator.reverseOrder()));
// Bob(30), Alex(30), Carl(25): возраст по убыванию, имя по убыванию

list.sort(Comparator.nullsFirst(Comparator.naturalOrder()));   // [null, a, b]
```

Полезные методы: `comparingInt` / `comparingDouble` (без упаковки), `thenComparing`, `reversed()`, `naturalOrder()`,
`reverseOrder()`, `nullsFirst` / `nullsLast`. Обратите внимание: `reversed()` разворачивает **всю цепочку**, собранную
до него, а не последний компаратор. Это аналог LINQ `OrderBy(...).ThenBy(...)` и `OrderByDescending`.

`list.sort(null)` сортирует по естественному порядку (нужен `Comparable`). `Collections.sort(list)` делает то же.

## Источники

- [Object as a Superclass, Java Tutorials](https://docs.oracle.com/javase/tutorial/java/IandI/objectclass.html) (10 мин)
- [`Object.equals` и `Object.hashCode`, Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Object.html) (10 мин, читать описание контрактов)
- [Object Ordering, Java Tutorials](https://docs.oracle.com/javase/tutorial/collections/interfaces/order.html) (10 мин)
- [`Comparator`, Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Comparator.html) (10 мин, статические методы `comparing`, `thenComparing`, `nullsFirst`)
- [`Comparable`, Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Comparable.html) (5 мин, про согласованность с `equals`)
- [`Map`, Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Map.html) (3 мин, предупреждение про изменяемые ключи)
- [`Objects`, Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Objects.html) (справочно: `equals`, `hash`, `requireNonNull`)
- [Lombok: `@EqualsAndHashCode`](https://projectlombok.org/features/EqualsAndHashCode) (5 мин)
- [How to define value equality for a type, C# (Microsoft Learn)](https://learn.microsoft.com/en-us/dotnet/csharp/programming-guide/statements-expressions-operators/how-to-define-value-equality-for-a-type) (10 мин, для сравнения)
- [`Object.GetHashCode`, C# (Microsoft Learn)](https://learn.microsoft.com/en-us/dotnet/api/system.object.gethashcode) (5 мин, для сравнения)
