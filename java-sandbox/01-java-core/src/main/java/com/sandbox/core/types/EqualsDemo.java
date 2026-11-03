package com.sandbox.core.types;

import java.util.Objects;

public class EqualsDemo {
    static void main() {

        Integer z1 = 127;
        long z2 = 127L;
        System.out.println("z1 == z2: " + (z1 == z2)); // true
        System.out.println("z1 equals z2: " + (z1.equals(z2))); // false

        // Integer Cache (пул/кэш целых чисел). кэш чисел от -128 до 127
        // При первом запуске JVM создает и сохраняет в память ровно по одному объекту Integer для каждого числа из этого диапазона.
        Integer z3 = 127;
        Integer z4 = 127;
        System.out.println("z3 == z4: " + (z3 == z4)); // true

        Integer z5 = 128; // => Integer a = Integer.valueOf(128);
        Integer z6 = 128; // => Integer a = Integer.valueOf(128);
        System.out.println("z5 == z6: " + (z5 == z6)); // false Разные объекты в куче
        System.out.println("z5 equals z6: " + z5.equals(z6)); // true Правильное сравнение

        int z7 = 128;
        int z8 = 128;
        System.out.println("z7 == z8: " + (z7 == z8)); // true
        System.out.println("z7 equals z8: " + ((Integer) z7).equals(z8)); // true Правильное сравнение

        // Безопасное сравнение при риске null
        String str1 = null;
        String str2 = "test";
        // str1.equals(str2) выбросит NullPointerException
        System.out.println("Objects.equals: " + Objects.equals(str1, str2)); // вернет false без ошибки
    }
}
