package com.sandbox.core;

import java.util.Objects;

public class Main {
    public static void main(String[] args) {

        System.out.println("--- TYPES ---");

        Byte byteEx = 0; // 8 бит
        System.out.println("Byte from %s to %s".formatted(Byte.MIN_VALUE, Byte.MAX_VALUE));
        Short shortEx = 0; // 16 бит
        System.out.println("Short from %s to %s".formatted(Short.MIN_VALUE, Short.MAX_VALUE));
        Integer integerEx = 0; // 32 бит
        System.out.println("Integer from %s to %s".formatted(Integer.MIN_VALUE, Integer.MAX_VALUE));
        Long longEx = 0L; // 64 бит
        System.out.println("Long from %s to %s".formatted(Long.MIN_VALUE, Long.MAX_VALUE));
        Float floatEx = 0.0f; // 32 бит
        System.out.println("Float from %s to %s".formatted(Float.MIN_VALUE, Float.MAX_VALUE));
        Double doubleEx = 0.0d; // 64 бит
        System.out.println("Double from %s to %s".formatted(Double.MIN_VALUE, Double.MAX_VALUE));
        Character characterEx = 0; // 16 бит
        System.out.println("Character from %s to %s".formatted(Character.MIN_VALUE, Character.MAX_VALUE));
        String stringEx = null; // char[] || byte[] => int * 16 =>  2 147 483 647 символов
        Boolean booleanEx = false; // 1 бит

        System.out.println("--- TYPES ---\n");

        System.out.println("--- EQUALS ---");

        // Integer Cache (пул/кэш целых чисел). кэш чисел от -128 до 127
        // При первом запуске JVM создает и сохраняет в память ровно по одному объекту Integer для каждого числа из этого диапазона.
        Integer a = 127;
        Integer b = 127;
        Integer c = 128; // => Integer a = Integer.valueOf(128);
        Integer d = 128; // => Integer a = Integer.valueOf(128);
        System.out.println("127 == 127: " + (a == b)); // true (работает )
        System.out.println("128 == 128: " + (c == d)); // false! Разные объекты в куче
        System.out.println("128 equals: " + c.equals(d)); // true! Правильное сравнение

        // Безопасное сравнение при риске null
        String str1 = null;
        String str2 = "test";
        // str1.equals(str2) выбросит NullPointerException
        System.out.println("Objects.equals: " + Objects.equals(str1, str2)); // вернет false без ошибки

        System.out.println("--- EQUALS ---\n");


        System.out.println("--- STRING ---");

        String name = "Alex";
        int age = 30;
        String msg = "Привет, %s! Тебе %d лет.".formatted(name, age);

        String json = """
        {
          "name": "%s"
        }
        """.formatted(name);

        StringBuilder sb = new StringBuilder();
        sb.append("a").append(1).append('b');
        String result = sb.toString();   // не забывайте toString()

        String csv = String.join(",", "a", "b", "c");   // как string.Join в C#
        String[] parts = csv.split(",");                 // как Split
        boolean has = csv.contains("b");
        String up = csv.toUpperCase();

        System.out.println("--- STRING ---\n");



    }
}
