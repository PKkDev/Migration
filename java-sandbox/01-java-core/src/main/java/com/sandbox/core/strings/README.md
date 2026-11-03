# Строки и текст

Код: [StringsDemo.java](StringsDemo.java), тест [TextStatsTest.java](../../../../../../test/java/com/sandbox/core/strings/TextStatsTest.java).

## `String`: неизменяемый объект

`String` это обычный класс (не алиас, как `string` в C#). Он **неизменяем**: любой метод (`toUpperCase`, `replace`,
`+`) возвращает **новую** строку, исходная остаётся прежней. Из этого вытекает пул строк, безопасность в многопоточности
и кэширование `hashCode`.

В отличие от C#, оператор `==` на строках сравнивает **ссылки**, а значения сравнивает `equals`.

## Пул строк (String Pool) и `intern()`

Строковые литералы хранятся в пуле (в куче). Два одинаковых литерала это один объект:

```java
String a = "java", b = "java";
a == b;                        // true: оба из пула
String c = new String("java");
a == c;                        // false: new всегда создаёт новый объект
a == c.intern();               // true: intern возвращает каноническую строку из пула
```

`intern()` нужен редко (экономия памяти на огромном числе повторяющихся значений). Сравнение значений всегда через
`equals`, а при риске `null` через `Objects.equals`.

### Константы времени компиляции

```java
"ja" + "va" == "java"          // true: компилятор склеивает константы на этапе сборки

String ja = "ja";              // не константа
ja + "va" == "java"            // false: склейка в рантайме создаёт новый объект

final String jaF = "ja";       // final + инициализатор-константа → константа времени компиляции
jaF + "va" == "java"           // true
```

Константа это `final`-переменная примитива или `String`, инициализированная константным выражением. Поле
`static final String VA = "va"` тоже константа (по соглашению такие имена пишут `ЗАГЛАВНЫМИ`), а
`static String va = "va"` нет, потому что оно изменяемо.

## Внутреннее устройство: Compact Strings (с Java 9)

`String` внутри хранит `byte[] value` и флаг кодировки `coder`:

- `LATIN1` (0): **1 байт на символ**, если *все* символы строки помещаются в Latin-1 (коды `0x00`…`0xFF`: ASCII и
  символы вроде `é`, `ü`);
- `UTF16` (1): **2 байта на символ**, если есть хотя бы один символ с кодом `> 0xFF` (кириллица, эмодзи, иероглифы).

Важно: это свойство **всей строки**. Миллион латинских букв занимает около 1 МБ, а стоит добавить один символ `я`,
как строка станет занимать около 2 МБ. Режим `UTF16` у всех строк включается флагом `-XX:-CompactStrings`.
Посмотреть можно в исходниках `String` (Ctrl+клик в IntelliJ): поля `value`, `coder`, метод `isLatin1()`.

В C# `string` всегда UTF-16, ничего подобного там нет.

## `char`, кодовые точки и графемы

`char` это 16-битная **единица UTF-16** (code unit), а не символ. Символы за пределами базовой плоскости (эмодзи,
редкие иероглифы) занимают **две** единицы, «суррогатную пару». Поэтому:

| Понятие | Метод | `"Hi 👋"` | `"👨‍👩‍👧"` |
|---|---|---|---|
| единицы UTF-16 | `length()` | 5 | 8 |
| кодовые точки Unicode | `codePointCount(0, length())` | 4 | 5 |
| символы глазами пользователя (графемы) | `BreakIterator.getCharacterInstance()` | 4 | 1 |

Семейный эмодзи 👨‍👩‍👧 состоит из пяти кодовых точек (три человека и два невидимых соединителя `ZWJ`), но на экране
выглядит как один символ. Для таких случаев нужен итератор границ графем:

```java
BreakIterator it = BreakIterator.getCharacterInstance();
it.setText(s);
int count = 0;
while (it.next() != BreakIterator.DONE) count++;   // 1 для "👨‍👩‍👧"
```

`getWordInstance()` это *границы слов*, он режет такую строку на три части: не тот итератор.

Полезные методы:

- `s.codePoints()` даёт `IntStream` кодовых точек, `Character.toString(cp)` превращает кодовую точку в строку;
- `Character.charCount(cp)` сообщает, сколько `char` занимает кодовая точка (1 или 2);
- при обходе `charAt(i)` по строке с эмодзи вы получаете половинки пар (в консоли они выводятся как `?`).

Сравнение с C#: `string.Length` врёт так же, как `length()`. Аналоги: `EnumerateRunes()` ↔ `codePoints()`,
`StringInfo` / `TextElementEnumerator` ↔ `BreakIterator`.

## Конкатенация и `StringBuilder`

`+` в Java 9+ компилируется в `invokedynamic` (`StringConcatFactory`): одно выражение с несколькими `+` собирается
эффективно, без цепочки промежуточных `StringBuilder`. Но **в цикле** `s += x` на каждой итерации создаёт новую строку и
копирует в неё всё накопленное: сложность O(n²). Прирост JIT-компилятора это не лечит.

Замер из `StringsDemo` (100 000 итераций, прогон 3 раза):

| Способ | Время |
|---|---|
| `s += i` | 2 000-5 000 мс (не ускоряется при повторах) |
| `StringBuilder.append(i)` | 0-1 мс |
| `String.join("-", готовыйСписок)` | 2-8 мс (один вызов на готовую коллекцию) |

Выводы: в цикле собирайте через `StringBuilder`, а готовую коллекцию склеивайте `String.join` или
`Collectors.joining`. `String.join` не заменяет `StringBuilder` в цикле: `s = String.join("", x, s)` снова копирует
всё каждый раз. Грубый замер через `System.nanoTime()` годится для порядка величин; для точных цифр нужен JMH.

`StringBuilder` не потокобезопасен (как и в C#). У него есть аналог `StringBuffer` с синхронизацией, на практике не нужный.

## Форматирование и текстовые блоки

```java
"Привет, %s! Тебе %d лет.".formatted(name, age);   // то же, что String.format(...)
```

`formatted` это аналог интерполяции `$"..."` из C#, но подстановка по позиции. Спецификаторы: `%s` строка, `%d` целое,
`%f` дробное, `%n` перевод строки текущей платформы (аргумента не берёт), `%%` сам знак процента.

**Текстовый блок** (`"""`, Java 15+) аналог raw-строки `"""` из C#. Правила:

- после открывающих `"""` обязателен перевод строки;
- компилятор убирает **общий минимальный отступ**: он считается по всем непустым строкам блока *включая* строку с
  закрывающими `"""`. Сдвинув закрывающие кавычки левее содержимого, вы оставите отступ у всех строк;
  сдвинув правее, ничего не измените;
- закрывающие `"""` на отдельной строке дают `\n` в конце текста, а в конце последней строки текста не дают;
- пробелы **в конце** каждой строки отбрасываются; `\s` это явный пробел, который не отбрасывается;
- `\` в конце строки означает «не вставлять перенос» (склейка строк);
- одиночные `"` экранировать не нужно, три подряд записываются как `\"""`.

```java
String q = """
        SELECT *
        FROM users \
        WHERE id = %d
        """.formatted(id);
```

## Локаль и регистр

`toLowerCase()` и `toUpperCase()` без аргумента используют **локаль по умолчанию**. На турецкой локали буква `I`
превращается в `ı` (без точки): `"TITLE".toLowerCase(tr)` даёт `"tıtle"`, а `"file".toUpperCase(tr)` даёт `"FİLE"`.
Из-за этого ломаются проверки вида `name.toUpperCase().equals("ADMIN")`.

Правило: для **машинных** строк (идентификаторы, ключи, протоколы, имена enum) указывайте `Locale.ROOT`;
локаль пользователя берут только для текста, который читает человек. В C# аналоги `ToLowerInvariant()` и
`ToUpperInvariant()`.

Создавать локаль: `Locale.forLanguageTag("tr")`, `Locale.of("ru", "RU")`. Конструкторы `new Locale(...)` объявлены
устаревшими с Java 19.

## `trim()` против `strip()`

| Метод | Что убирает по краям |
|---|---|
| `trim()` | символы с кодом `<= U+0020` (пробел, табуляция, переводы строк, управляющие) |
| `strip()` | все Unicode-пробелы по `Character.isWhitespace` (например, `U+2003` EM SPACE) |

Неразрывные пробелы (`U+00A0`, `U+2007`, `U+202F`) **не убирает ни тот, ни другой**: `isWhitespace` их сознательно
исключает. Поэтому и `" ".isBlank()` равно `false`. Это частая ловушка с данными из Excel и веб-форм. Оба метода
работают только с краями строки, середину не трогают.

## Кодировка по умолчанию

С Java 18 кодировка по умолчанию UTF-8 на всех платформах (JEP 400). В `build.gradle.kts` проекта UTF-8 задан
явно для компиляции и запуска (`-Dfile.encoding=UTF-8`, `-Dsun.stdout.encoding=UTF-8`), чтобы кириллица и эмодзи
печатались в консоли корректно.

## Источники

- [Strings, Java Tutorials](https://docs.oracle.com/javase/tutorial/java/data/strings.html) (10 мин)
- [Characters, Java Tutorials](https://docs.oracle.com/javase/tutorial/java/data/characters.html) (5 мин)
- [The Absolute Minimum Every Software Developer Must Know About Unicode (Joel Spolsky)](https://www.joelonsoftware.com/2003/10/08/the-absolute-minimum-every-software-developer-absolutely-positively-must-know-about-unicode-and-character-sets-no-excuses/) (20 мин)
- [Supplementary Characters as Surrogates, Java Tutorials](https://docs.oracle.com/javase/tutorial/i18n/text/supplementaryChars.html) (7 мин)
- [JEP 254: Compact Strings](https://openjdk.org/jeps/254) (7 мин)
- [JEP 280: Indify String Concatenation](https://openjdk.org/jeps/280) (4 мин)
- [Programmer's Guide to Text Blocks](https://docs.oracle.com/en/java/javase/25/text-blocks/index.html) (10 мин)
- [UAX #29: Unicode Text Segmentation](https://unicode.org/reports/tr29/) (справочно: что такое графемный кластер)
- [`BreakIterator`, Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/text/BreakIterator.html) (справочно)
- [JEP 400: UTF-8 by Default](https://openjdk.org/jeps/400) (5 мин)
- [Класс `String`, Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/String.html) (справочно: `codePoints`, `intern`, `formatted`, `strip`)
