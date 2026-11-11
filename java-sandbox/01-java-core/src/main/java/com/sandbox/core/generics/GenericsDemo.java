package com.sandbox.core.generics;

import com.sandbox.core.common.ConsoleColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GenericsDemo {
    record Pair<A, B>(A first, B second) {
        <C> Pair<A, C> withSecond(C c) {
            return new Pair<>(first, c);
        }
    }

    class Box<T> {
        void f(Object o) {
            // В Java дженерики существуют только для компилятора.
            // После компиляции T заменяется на Object (или на свою верхнюю границу), а List<String> превращается в обычный List.
            // В рантайме JVM вообще не знает, чем был T.
            // Сделано это ради обратной совместимости с кодом до Java 5.

            // T x = new T(); // Type parameter 'T' cannot be instantiated directly
            // T[] arr = new T[1]; // Type parameter 'T' cannot be instantiated directly
            // boolean b = o instanceof List<String>; // 'Object' cannot be safely cast to 'List<String>'
        }
    }

    // Result
    sealed interface Result<T> permits Ok, Err {
    }

    record Ok<T>(T value) implements Result<T> {
    }

    record Err<T>(String error) implements Result<T> {
    }

    static String mapResult(Result<Integer> result) {
        String text = switch (result) {
            case Ok<Integer>(Integer value) -> "Успех [" + value + "]: ";
            case Err<Integer>(String error) -> "Фатальная ошибка: " + error;
        };
        return text;
    }

    public static Result<Integer> genericParse(String s) {

        try {
            var result = Integer.parseInt(s);
            return new Ok<>(result);
        } catch (NumberFormatException e) {
            return new Err<>(e.toString());
        }
    }
    // Result

    static <T> void copy(List<? super T> dst, List<? extends T> src) {
        for (int i = 0; i < src.size(); i++) {
            dst.add(src.get(i));
        }
    }

    static void print(String name, List<?> list) {
        String result = list.stream()
                .map(String::valueOf) // или .map(Object::toString)
                .collect(Collectors.joining(", "));
        System.out.println(name + ": " + result);
    }

    static <T extends Comparable<T>> T max(List<T> list) {
        return Collections.max(list);
    }

    static void main(String[] args) {

        // 1. Стирание
        List<String> list1 = new ArrayList<String>();
        List<Integer> list2 = new ArrayList<Integer>();

        System.out.println("list1.getClass(): " + list1.getClass()); // java.util.ArrayList
        System.out.println("list2.getClass(): " + list2.getClass()); // java.util.ArrayList
        System.out.println("list1.getClass() = list2.getClass(): " + (list1.getClass() == list2.getClass())); //true

        // 2. Pair<A, B>
        Pair<String, Integer> pair = new Pair<>("STR", 1);
        System.out.println("pair: " + pair); // Pair[first=STR, second=1]
        var second = pair.withSecond("xxxx");
        System.out.println("second: " + second); // Pair[first=STR, second=xxxx]

        // 3. Result<T>
        var parse1 = genericParse("55"); // успешно
        ConsoleColor.greenLog("parse('55'): " + mapResult(parse1));

        var parse2 = genericParse("55kk"); // ошибка
        ConsoleColor.redLog("parse('55kk'): " + mapResult(parse2));

        // 4. Copy и PECS
        List<Integer> list3 = new ArrayList<>(List.of(1, 2, 3));
        print("list3", list3);
        List<Number> list4 = new ArrayList<Number>();
        List<Object> list5 = new ArrayList<Object>();

        copy(list4, list3);
        print("list4", list4);
        copy(list5, list3);
        print("list5", list5);

        list3.add(66);
        print("list3 + list3.add(66)", list3);
        print("list4 + list3.add(66)", list4);
        print("list5 + list3.add(66)", list5);

        list4.add(66);
        print("list3 + list4.add(66)", list3);
        print("list4 + list4.add(66)", list4);
        print("list5 + list4.add(66)", list5);

        list5.add(66);
        print("list3 + list5.add(66)", list3);
        print("list4 + list5.add(66)", list4);
        print("list5 + list5.add(66)", list5);

        List<? extends Number> producer = new ArrayList<Integer>(List.of(1, 2));
        Number n = producer.get(0); // читать можно
        //producer.add(3); // нельзя писать, потому что компилятор не знает точный тип списка (это может быть List<Double>)

        List<? super Integer> consumer = new ArrayList<Number>();
        consumer.add(5); // писать можно
        // Integer i = consumer.get(0); // нельзя читать как Integer, потому что в списке могут быть любые предки Integer
        Object o = consumer.get(0); // читать можно только как Object

        // 5. Ограничение
        List<String> list6 = new ArrayList<String>(List.of("1", "2", "3", "a"));
        List<Integer> list7 = new ArrayList<Integer>(List.of(1, 2, 3));
        List<Object> list8 = new ArrayList<Object>(List.of("1", new Ok<>(5), new Err<>("Error")));

        System.out.println("max(list6) " + max(list6)); // "a"
        System.out.println("max(list7) " + max(list7)); // 3
        //System.out.println("max(list8) " + max(list8)); // reason: no instance(s) of type variable(s) T exist so that Object conforms to Comparable<T>

        // 6. Heap pollution
        List<String> list9 = new ArrayList<>(List.of("a"));
        print("list9", list9);
        List raw = list9; // сырой тип: IDE подсветит предупреждение
        raw.add(1); // успешно
        print("list9 + raw.add(1)", list9);
        System.out.println(list9.size()); // 2
        try {
            String s = list9.get(1); // Exception in thread "main" java.lang.ClassCastException
        } catch (Exception e) {
            ConsoleColor.redLog("list9.get(1): " + e);
        }

    }
}

/*

Что такое стирание типов и почему List<String> и List<Integer> в рантайме один класс?
В рантайме не существует generic. T заменяется на Object (или на свою верхнюю границу), а List<String> превращается в обычный List

Почему List<Integer> нельзя присвоить List<Number>, а массивы так можно?
Дженерики проверяются только при компиляции (потом типы стираются), и в рантайме поймать такую ошибку было бы нечем.
Поэтому List<Integer> не является подтипом List<Number> (это называется инвариантность).
Нужно ковариантное чтение? Берите List<? extends Number> (читать можно, писать нельзя).
Почему массивам можно. Массивы появились в Java 1.0, до дженериков, и тогда их сделали ковариантными: String[] считается подтипом Object[].
Безопасность обеспечивается проверкой в рантайме, потому что массив помнит тип своих элементов (массивы reified):
Итого: у списков ошибку отсекает компилятор (поэтому они инвариантны), у массивов её ловит JVM в рантайме (поэтому они ковариантны).

Как работает правило PECS, и когда ? extends, а когда ? super?
PECS разделяет работу с коллекциями на два сценария: когда вы только читаете из них данные (Producer) и когда только записываете (Consumer)
<? extends Number> - нельзя писать, потому что компилятор не знает точный тип списка (это может быть List<Double>)
<? super Integer> - нельзя читать как Integer, потому что в списке могут быть любые предки Integer

Чем дженерики Java отличаются от дженериков C#?
В рантайме не существует generic. В C# существуют.
В C# дженерики реифицированы: List<string> и List<int> остаются разными типами и во время выполнения.
Поэтому там работают new T() (с ограничением where T : new()), typeof(T) и is List<string>.

Что такое heap pollution и почему ошибка появляется не там, где совершена?
Heap pollution это ситуация, когда переменная типа List<String> на самом деле содержит не-строки.
Для этого достаточно сырого типа (raw type, List без параметров) или небезопасного приведения
А проявится она только тогда, когда мы захотим получить это значение
*/