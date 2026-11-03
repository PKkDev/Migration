package com.sandbox.core.types;

import com.sandbox.core.common.ConsoleColor;

import java.util.*;

public class TypesDemo {


    public static void main(String[] args) {

        System.out.println("--- TYPES ---");

        // У обёрток значение по умолчанию null, а примитивы всегда имеют значение.
        // Integer => null
        // int => 0

        Byte byteEx = 0; // 8 бит
        System.out.println("Byte from %s to %s".formatted(Byte.MIN_VALUE, Byte.MAX_VALUE));
        Short shortEx = 0; // 16 бит
        System.out.println("Short from %s to %s".formatted(Short.MIN_VALUE, Short.MAX_VALUE));
        Integer integerEx = 0; // 32 бит
        System.out.println("Integer from %s to %s".formatted(Integer.MIN_VALUE, Integer.MAX_VALUE));
        Long longEx = 0L; // 64 бит
        System.out.println("Long from %s to %s".formatted(Long.MIN_VALUE, Long.MAX_VALUE));
        Float floatEx = 0.0f; // 32 бит знак хранится отдельным битом
        System.out.println("Float from %s to %s".formatted(-Float.MAX_VALUE, Float.MAX_VALUE));
        Double doubleEx = 0.0d; // 64 бит знак хранится отдельным битом
        System.out.println("Double from %s to %s".formatted(-Double.MAX_VALUE, Double.MAX_VALUE));
        Character characterEx = 0; // 16 бит
        System.out.println("Character from %s to %s".formatted((int) Character.MIN_VALUE, (int) Character.MAX_VALUE));
        String stringEx = null; // char[] || byte[] => int * 16 =>  2 147 483 647 символов
        Boolean booleanEx = false; // 1 бит

        String t1 = Integer.toUnsignedString(2147483647 * 2);
        System.out.println("toUnsignedString %s".formatted(t1));

        Integer t2 = Integer.divideUnsigned(9, 3);
        System.out.println("divideUnsigned %s".formatted(t2));

        try {
            int x = (Integer) null;
        } catch (NullPointerException e) {
            ConsoleColor.redLog("catch " + NullPointerException.class.getName());
        }

        try {
            Map<Integer, Integer> mapForEx = new HashMap<>();
            mapForEx.put(1, 1);
            mapForEx.get((Integer) null);
            var g = mapForEx.get(2); // Exception не будет т.к. Integer(var) допускает null
            int v = mapForEx.get(2); // Exception будет т.к. int допускает null
        } catch (NullPointerException e) {
            ConsoleColor.redLog("catch " + NullPointerException.class.getName());
        }

        try {
            var flag = false;
            Integer boxed = null;
            Integer a = flag ? 1 : null;   // boxed распаковывается в int, а int не может быть null
        } catch (NullPointerException e) {
            ConsoleColor.redLog("catch " + NullPointerException.class.getName());
        }

        var intMaxPlusOne1 = Integer.MAX_VALUE + 1; // переполнили => минимальное значение
        System.out.println("intMaxPlusOne1: " + intMaxPlusOne1);

        try {
            var intMaxPlusOne2 = Math.addExact(Integer.MAX_VALUE, 1); // переполнение => ArithmeticException
            System.out.println("intMaxPlusOne2: " + intMaxPlusOne2);
        } catch (ArithmeticException ex) {
            ConsoleColor.redLog("ArithmeticException: " + ex);
        } catch (Exception ex) {
            ConsoleColor.redLog("Exception: " + ex);
        }

        System.out.println("--- TYPES ---\n");

        System.out.println("--- METHOD PARAMS ---\n");

        var g = 1;
        System.out.println("g: " + g);
        patchNumber(g); // Передали копию значения => исходное значение не изменилось
        System.out.println("after patchNumber g: " + g);

        var s = "ss";
        System.out.println("s: " + s);
        patchString(s); // Передали копию значения => исходное значение не изменилось
        System.out.println("after patchString s: " + s);

        var l1 = new ArrayList<Integer>();
        l1.add(1);
        System.out.println("l1: " + l1);
        patchList(l1); // Передали копию ссылки => изменилось исходное значение
        System.out.println("after patchList l1: " + l1);

        var l2 = new ArrayList<Integer>();
        l2.add(1);
        System.out.println("l2: " + l2);
        reassignList(l2); // Передали копию ссылки => внутри создали новый объект => исходное значение не изменилось
        System.out.println("after reassignList l2: " + l2);


        System.out.println("--- METHOD PARAMS ---\n");

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

    private static void patchNumber(Integer n) {
        n++; // n++ превращается в n = Integer.valueOf(n + 1)
    }

    private static void patchString(String s) {
        s += "test"; // создаём новую строку
    }

    private static void patchList(List<Integer> s) {
        s.add(55); // изменяет объект, на который указывает копия ссылки, поэтому изменение видно снаружи
    }

    private static void reassignList(List<Integer> s) {
        s = new ArrayList<>();   // копия ссылки теперь указывает на другой объект
        s.add(99);               // оригинал не изменился
    }
}
