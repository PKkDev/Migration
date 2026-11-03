# Коллекции (Java Collections Framework)

Код: [CollectionsDemo.java](CollectionsDemo.java).

## Две ветви

Все структуры данных объединены в **Java Collections Framework (JCF)** и делятся на две ветви:

1. **`Collection`**: одиночные элементы. Потомки: `List` (порядок, дубликаты, доступ по индексу), `Set` (уникальные
   элементы), `Queue` / `Deque` (очереди и стеки).
2. **`Map`**: пары «ключ → значение». Формально не наследует `Collection`, но входит во фреймворк.

В коде вы почти всегда **объявляете интерфейс, а создаёте реализацию**:

```java
List<String> names = new ArrayList<>();   // тип слева интерфейс, справа реализация
```

Так можно заменить реализацию, не трогая остальной код. В C# похожий принцип, но привычка писать `List<T>` слева там
распространена шире. Исключение: когда нужны методы именно реализации (например, `LinkedList` как `Deque`).

**Дженерики не принимают примитивы:** `List<Integer>`, а не `List<int>`. Каждое число упаковывается в объект (см. раздел
про обёртки в пакете `types`). Для больших числовых массивов это дорого по памяти и времени.

## Сравнение с C#

| C# | Java |
|---|---|
| `List<T>` | `ArrayList<T>` (интерфейс `List<T>`) |
| `LinkedList<T>` | `LinkedList<T>` |
| `Dictionary<K,V>` | `HashMap<K,V>` |
| `SortedDictionary<K,V>` | `TreeMap<K,V>` |
| `HashSet<T>` | `HashSet<T>` |
| `SortedSet<T>` | `TreeSet<T>` |
| `Queue<T>`, `Stack<T>` | `ArrayDeque<T>` (и очередь, и стек); не использовать старый класс `Stack` |
| `PriorityQueue<T,P>` | `PriorityQueue<T>` (приоритет через `Comparable` или `Comparator`) |
| `ConcurrentDictionary<K,V>` | `ConcurrentHashMap<K,V>` |
| `ImmutableList<T>` | `List.of(...)`, `List.copyOf(...)` |
| `list.Count`, `list[i]` | `list.size()`, `list.get(i)` |
| `dict[key]` бросает `KeyNotFoundException` | `map.get(key)` **возвращает `null`** |
| LINQ | Stream API |

## Реализации и когда какую брать

| Нужно | Реализация | Особенности |
|---|---|---|
| Динамический список | `ArrayList` | массив внутри; `get(i)` O(1), добавление в конец амортизированно O(1), вставка/удаление в середине O(n) |
| Список как очередь с двух концов | `LinkedList` | узлы-связи; `get(i)` O(n); память на узел больше; почти всегда проигрывает `ArrayList` и `ArrayDeque` |
| Уникальные элементы | `HashSet` | на `HashMap`; порядок не определён; операции O(1) в среднем |
| Уникальные элементы + порядок вставки | `LinkedHashSet` | хеш-таблица + связный список |
| Отсортированные элементы | `TreeSet` | красно-чёрное дерево; операции O(log n); элементы должны быть сравнимы |
| Словарь | `HashMap` | порядок не определён; допускает один `null`-ключ и `null`-значения |
| Словарь + порядок вставки | `LinkedHashMap` | основа для простого LRU-кэша |
| Словарь с сортировкой по ключу | `TreeMap` | O(log n); `null`-ключ запрещён |
| Очередь или стек (FIFO / LIFO) | `ArrayDeque` | быстрее `LinkedList`; `null` запрещён |
| Очередь по приоритету | `PriorityQueue` | на куче; `peek` O(1), вставка O(log n) |

В `CollectionsDemo` часть переменных объявлена так: `Queue<Integer> q = new ArrayDeque<>()` (очередь: `offer` / `poll`)
и `Deque<Integer> d = new ArrayDeque<>()` (стек: `push` / `pop`, очередь с обоих концов: `addFirst` / `addLast`).

## Порядок и `HashMap`

`HashMap` не гарантирует порядок итерации, и он может отличаться между запусками и версиями JDK. Не полагайтесь на
него. Когда порядок важен, берите `LinkedHashMap` (по вставке) или `TreeMap` (по ключу).

Как устроен `HashMap` (подробнее в Дне 9): массив корзин, индекс считается по `hashCode()` ключа, при коллизиях в
корзине список, а при длинной цепочке дерево; при заполнении сверх коэффициента загрузки (0,75) массив удваивается.
Поэтому у ключа должны быть согласованные `equals` и `hashCode` (День 4).

## Операции, которые использованы в коде

```java
List<String> names = new ArrayList<>();
names.add("Bob");  names.get(0);  names.size();  names.contains("Alex");

names.remove("Bob");        // remove(Object): удаляет ЭЛЕМЕНТ
List<Integer> nums = new ArrayList<>(List.of(10, 20, 1));
nums.remove(1);             // remove(int index): удаляет по ИНДЕКСУ → [10, 1]
nums.remove(Integer.valueOf(1));   // удаляет число 1 → [10]

Map<String, Integer> ages = new HashMap<>();
ages.put("Alex", 30);
ages.get("Nobody");                // null, исключения нет
ages.getOrDefault("Nobody", 0);    // 0
ages.containsKey("Bob");
for (Map.Entry<String, Integer> e : ages.entrySet()) { e.getKey(); e.getValue(); }
```

Ловушка с перегрузкой: `list.remove(1)` на `List<Integer>` удаляет **элемент с индексом 1**, а не число 1. Чтобы
удалить число, нужна упаковка (`Integer.valueOf(1)`).

Результаты вызовов вроде `ages.get("Alex")` в одиночку ничего не печатают, ими пользуются через переменную или `println`.

## Неизменяемые коллекции (Java 9+)

```java
List<String> list = List.of("A", "B", "C");
Set<Integer> set = Set.of(1, 2, 3);
Map<String, String> map = Map.of("key1", "val1", "key2", "val2");
```

Их **нельзя менять**: `add`, `remove`, `put` бросают `UnsupportedOperationException`. Особенности (проверено запуском):

- `null`-элементы запрещены: `List.of("a", null)` бросает `NullPointerException`; даже `List.of(1).contains(null)` бросает NPE;
- `Set.of(1, 1)` и `Map.of` с повторяющимся ключом бросают `IllegalArgumentException`;
- порядок итерации в `Set.of` и `Map.of` **намеренно перемешивается между запусками JVM**;
- `List.copyOf(src)` делает неизменяемую **копию**, а `Collections.unmodifiableList(src)` даёт только
  **неизменяемое представление**: если изменить исходный список, представление это увидит (в проверке:
  `view=[1, 2, 3]`, `copy=[1, 2]`).

## Потокобезопасные коллекции (`java.util.concurrent`)

Обычные коллекции (`ArrayList`, `HashMap`) **не потокобезопасны**. Для параллельного доступа:

| Класс | Назначение |
|---|---|
| `ConcurrentHashMap` | потокобезопасный словарь; запрещает `null`-ключи и значения; атомарные `compute`, `merge`, `putIfAbsent` |
| `CopyOnWriteArrayList` | при каждой записи копирует массив; удобен, когда читают часто, а пишут редко |
| `ArrayBlockingQueue` | блокирующая очередь **ограниченного** размера (в коде 5); `put` ждёт место, `take` ждёт элемент |
| `LinkedBlockingQueue` | блокирующая очередь (по умолчанию без ограничения) |

Блокирующие очереди основа схемы «производитель-потребитель». Подробно это тема Дней 12-13.

## Stream API против LINQ

```java
// C#: names.Where(n => n.Length > 3).Select(n => n.ToUpper()).ToList()
List<String> res = names.stream()
        .filter(n -> n.length() > 3)
        .map(String::toUpperCase)
        .toList();
```

Отличия от LINQ:

- у LINQ источник можно перечислять сколько угодно раз, а `Stream` **одноразовый**: второй терминальный вызов бросает `IllegalStateException`;
- операции ленивые, пока нет терминальной (`toList`, `collect`, `forEach`, `count`) ничего не выполняется;
- `.toList()` (Java 16+) возвращает **неизменяемый** список;
- `String::toUpperCase` это ссылка на метод, аналог группы методов в C#.

Подробнее Stream API в Дне 10.

## Почему файл называется `CollectionsDemo`

Класс `java.util.Collections` (статические утилиты: `sort`, `unmodifiableList`, `emptyList`) подключается автоматически
через `import java.util.*`. Собственный класс `Collections` в вашем пакете **перекрывает** его в этом пакете: вызов
`Collections.sort(list)` стал бы обращением к вашему классу. Поэтому имена, совпадающие с классами JDK, лучше
избегать.

## Источники

- [Collections Framework Overview](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/doc-files/coll-overview.html) (15 мин)
- [Lesson: Collections, Java Tutorials](https://docs.oracle.com/javase/tutorial/collections/index.html) (30 мин, разделы Interfaces, Implementations)
- [Collections Framework, dev.java](https://dev.java/learn/api/collections-framework/) (15 мин)
- [Creating Immutable Lists, Sets, and Maps](https://docs.oracle.com/en/java/javase/25/core/creating-immutable-lists-sets-and-maps.html) (10 мин)
- [Пакет `java.util.concurrent`, Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/package-summary.html) (справочно)
- [Streams, dev.java](https://dev.java/learn/api/streams/) (15 мин, предварительно перед Днём 10)
