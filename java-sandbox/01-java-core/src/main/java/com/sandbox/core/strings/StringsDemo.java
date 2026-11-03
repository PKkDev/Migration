package com.sandbox.core.strings;

import java.text.BreakIterator;
import java.time.Duration;
import java.util.*;

import static com.sandbox.core.common.ConsoleColor.greenLog;

public class StringsDemo {

    private static String va1 = "va";
    private static final String va2 = "va";

    static void main() {

        greenLog("---1.1---");
        // Возьмите строку `"Hi 👋 Привет 🌍"`.
        // - Выведите `length()` и `codePointCount`. Сколько получилось и почему они разные?
        // - Переберите строку циклом по `charAt` и распечатайте символы: что вы увидите вместо эмодзи?
        //         - Посчитайте частоты символов в `Map<Integer, Integer>` по кодовым точкам (подсказка: `codePoints()` и `merge`). Вывести ключи нужно как символы: `Character.toString(cp)`.
        // - Контрольные цифры для `"👨‍👩‍👧"`: `length()` = 8, кодовых точек 5, «символов на экране» 1 (через `BreakIterator`).

        var testStr1 = "Hi 👋 Привет 🌍";
        System.out.println("testStr1 | string: " + testStr1);
        System.out.println("testStr1 | length: %s".formatted(testStr1.length())); // 15
        System.out.println("testStr1 | codePointCount: %s".formatted(testStr1.codePointCount(0, testStr1.length()))); // 13

        // 15 -> 13
        // `char` — это 16-битная единица UTF-16
        // т.к. 15 (Unicode code units в строке <=> единицы UTF-16), а 13 (Unicode code points в строке <=> кодовые точки)
        // 1 кодовая точка не всегда = символу в строке
        // 🌍 и 👋 = 1 Unicode code points = 2 Unicode code units
        greenLog("---1.1---");

        greenLog("---1.2---");
        for (char symbol : testStr1.toCharArray()) {
            System.out.print("%s + ".formatted(symbol));
        }
        System.out.println("");
        for (int i = 0; i < testStr1.length(); i++) {
            System.out.print("%s + ".formatted(testStr1.charAt(i)));
        }
        System.out.println("");
        // 2 пробедбных символа для каждого из 🌍 и 👋 - как раз 1 Unicode code points = 2 Unicode code units
        greenLog("---1.2---");

        greenLog("---1.3---");
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        testStr1.codePoints().forEach(cp -> frequencyMap.merge(cp, 1, Integer::sum));
        frequencyMap.forEach((cp, count) -> {
            String character = Character.toString(cp);
            System.out.println("Символ: '" + character + "' -> Частота: " + count);
        });
        greenLog("---1.3---");

        greenLog("---1.4---");
        var testStr2 = "👨‍👩‍👧";
        System.out.println("testStr2 | string: " + testStr2);
        System.out.println("testStr2 | length: %s".formatted(testStr2.length())); // 8
        System.out.println("testStr2 | codePointCount: %s".formatted(testStr2.codePointCount(0, testStr2.length()))); // 5


        // (должно быть `1`), через `BreakIterator.getCharacterInstance()`. Схема: `setText(s)`, затем считаем вызовы `next()` до возврата `BreakIterator.DONE`.
        // Я проверил, что это даёт `1` для `👨‍👩‍👧`.
        BreakIterator boundary = BreakIterator.getCharacterInstance(Locale.forLanguageTag("ru-RU"));
        // BreakIterator.getCharacterInstance => итератор границ символов
        boundary.setText(testStr2);

        int start = boundary.first();
        for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
            String word = testStr2.substring(start, end);
            System.out.println("Слово/фрагмент: [" + word + "]"); // 1
        }

        System.out.println("---1.4---");

        greenLog("---2---");

        // Напишите четыре сравнения и предскажите результат до запуска:
        // - два одинаковых литерала через `==`;
        // - литерал и `new String("java")`;
        // - то же, но после `.intern()`;
        // - `"ja" + "va"` против `"java"`, затем то же с локальной переменной `String ja = "ja"` (с `final` и без).

        char a1 = 'a';
        char a2 = 'a';
        System.out.println("a1 == a2: %s".formatted(a1 == a2)); // true

        String a3 = "java";
        String a4 = new String("java");
        System.out.println("a3 == a4: %s".formatted(a3 == a4)); // false
        System.out.println("a3.intern() == a4.intern(): %s".formatted(a3.intern() == a4.intern())); // true

        String a5 = "java";
        String a6 = "java";
        System.out.println("a5 == a6: %s".formatted(a5 == a6)); // true
        System.out.println("a5.intern() == a6.intern(): %s".formatted(a5.intern() == a6.intern())); // true

        String a7 = "ja" + "va"; // <=> "java"
        String a8 = "java";
        System.out.println("a7 == a8: %s".formatted(a7 == a8)); // true

        String a9 = "ja" + va1; // <=> private static String va1 = "va";
        String a10 = "java";
        System.out.println("a9 == a10: %s".formatted(a9 == a10)); // false

        String a11 = "ja" + va2; // <=> private static final String va2 = "va";
        String a12 = "java";
        System.out.println("a11 == a12: %s".formatted(a11 == a12)); // true
        greenLog("---2---");

        greenLog("---3---");

        // Замерьте через `System.nanoTime()`
        // три варианта: `s += "x"`, `StringBuilder` и `String.join`.
        // Запустите каждый по три раза подряд и обратите внимание, как меняется время (прогрев JIT).
        // Замер получится грубым, это нормально: честный бенчмарк делают через JMH, мы вернёмся к нему позже.

        var count = 3;
        for (int i = 0; i < count; i++) {
            checkIteration();
        }

        greenLog("---3---");

        greenLog("---4---");

        // Откройте `String.java` в IntelliJ, найдите `coder` и методы `isLatin1isLatin1()`.
        // Запишите комментарием в коде, в каких двух случаях строка хранится как UTF-16.

        //Ответ: это всё история про кодировку/хранение строк. Только латинские символы занимают 1 байт,
        // а другие (например Кириллица) уже по 2 байта на символ
        // т.е. в зависимости от содержимого строки, она будет занимать разное кол-во памяти и закодирвоаная по-разному
        // В строке есть хотя бы один символ вне Latin-1**, то есть код > `0xFF`: кириллица, эмодзи, иероглифы. Тогда *вся* строка хранится по 2 байта на символ, а не только эти символы. Латиница с диакритикой (`é`, `ü`) в Latin-1 ещё помещается, не только ASCII.
        // Включён флаг JVM `-XX:-CompactStrings`**: тогда любая строка хранится как UTF-16.

        greenLog("---4---");

        greenLog("---5---");
        // Сделайте JSON-блок с `formatted(...)`, затем подвигайте закрывающие `"""` влево и вправо и посмотрите,
        // как меняется отступ.
        // Попробуйте `\` в конце строки (склейка) и `\s` (сохранить пробел в конце).

        String name = "Alex";
        String json = """
                {
                  "name": "%s"
                }
                """.formatted(name);
        show("A", json);

        String a = """
                {
                  "x": 1
                }
                """;
        show("B", a);
        // не понял, что требуется


        greenLog("---5---");

        greenLog("---6---");
        // Выведите `"TITLE".toLowerCase(Locale.forLanguageTag("tr"))` и `"TITLE".toLowerCase(Locale.ROOT)`.
        // Заодно сравните `trim()` и `strip()` на строке с неразрывным пробелом `\u00A0`.

        System.out.println("Locale.forLanguageTag(\"tr\"): " + "TITLE".toLowerCase(Locale.forLanguageTag("tr"))); // точка над «i» теряется
        System.out.println("Locale.ROOT: " + "TITLE".toLowerCase(Locale.ROOT));

        var strForTrim = "\u2003abc\u2003";
        System.out.println("strForTrim: " + strForTrim);
        System.out.println("strForTrim.trim(): " + strForTrim.trim()); // нет разницы
        System.out.println("strForTrim.strip(): " + strForTrim.strip()); // нет разницы

        greenLog("---6---");

        // Самопроверка
        // Почему "Hi 👋".length() равно 5 (Unicode code units), а Unicode code points  = 4 т.к. поинт(эмодзи занимает 2 символа)
        // как получить честное число кодовых точек? - через юcodePointCount()

        // Почему s += x в цикле медленный, если + компилируется в invokedynamic?
        // на каждой итерации создаётся новая строка, в которую копируется содержимое предыдущей => O(n²)

        // Чем toUpperCase() может подвести на сервере с нестандартной локалью и как этого избежать?
        // неверное преобразование регистра
        // избежать - указывать нужную культуру
    }

    private static void show(String title, String s) {
        System.out.println("== " + title);
        System.out.println(s.replace(" ", "·").replace("\n", "⏎\n") + "|КОНЕЦ");
    }

    private static void checkIteration() {
        var st1 = System.nanoTime();
        var str1 = "";
        for (int i = 0; i < 100000; i++) {
            str1 += i;
        }
        var st2 = System.nanoTime();
        System.out.println(str1.length() + " '+=' за: %s мс".formatted(Duration.ofNanos(st2 - st1).toMillis()));

        var st3 = System.nanoTime();
        StringBuilder str2 = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            str2.append(i);
        }
        var st4 = System.nanoTime();
        System.out.println(str2.length() + " 'StringBuilder' за: %s мс".formatted(Duration.ofNanos(st4 - st3).toMillis()));

        List<String> str3 = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            str3.add(String.valueOf(i));
        }
        var st5 = System.nanoTime();
        String result = String.join("-", str3);
        var st6 = System.nanoTime();
        System.out.println(result.length() + " 'String.join' за: %s мс".formatted(Duration.ofNanos(st6 - st5).toMillis()));
    }
}