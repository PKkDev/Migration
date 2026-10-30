package com.sandbox.core;

import java.util.*;
import java.util.concurrent.*;

public class CollectionsDemo {
    static void main(String[] args) {
        // В Java все структуры данных объединены в Java Collections Framework (JCF).
        // Они делятся на две основные ветви:
        //  1) Интерфейс Collection — для одиночных элементов (списки, множества, очереди).
        //  2) Интерфейс Map — для пар «ключ — значение» (формально не наследует Collection, но считается частью фреймворка).

        // List (Списки)
        List<Integer> arrayList = new ArrayList<>();
        LinkedList<Integer> linkedList = new LinkedList<>();

        // Set (Множества)
        Set<Integer> hashSet = new HashSet<>();
        Set<Integer> linkedHashSet = new LinkedHashSet<>();
        Set<Integer> treeSet = new TreeSet<>();

        // Queue и Deque (Очереди и двухсторонние очереди)
        Queue<Integer> queueArrayDeque = new ArrayDeque<>();
        Queue<Integer> queuePriorityQueue = new PriorityQueue<>();

        Deque<Integer> dequeArrayDeque = new ArrayDeque<>();

        // Map (Ассоциативные массивы / Словари)
        Map<Integer, String> hashMap = new HashMap<>();
        Map<Integer, String> linkedHashMap = new LinkedHashMap<>();
        Map<Integer, String> treeMap = new TreeMap<>();

        // Потокобезопасные коллекции (java.util.concurrent)
        Map<Integer, String> concurrentHashMap = new ConcurrentHashMap<>();
        List<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        BlockingQueue<Integer> arrayBlockingQueue = new ArrayBlockingQueue<>(5);
        BlockingQueue<Integer> linkedBlockingQueue = new LinkedBlockingQueue<>();

        // Неизменяемые коллекции (Java 9+) (UnsupportedOperationException)
        List<String> list = List.of("A", "B", "C");
        Set<Integer> set = Set.of(1, 2, 3);
        Map<String, String> map = Map.of("key1", "val1", "key2", "val2");

        // Нужен динамический список                            -> ArrayList
        // Нужна коллекция уникальных элементов	                -> HashSet
        // Уникальные элементы с сохранением порядка вставки    -> LinkedHashSet
        // Элементы или ключи должны быть отсортированы         -> TreeSet / TreeMap
        // Словарь «ключ — значение»                            -> HashMap
        // Очередь или стек (FIFO / LIFO)                       -> ArrayDeque
        // Словарь для параллельных потоков                     -> ConcurrentHashMap

        // names
        List<String> names = new ArrayList<>();
        names.add("Bob");
        names.add("Alex");
        names.add("Carl");
        System.out.printf("get(0): %s%n", names.get(0));          // "Bob"
        System.out.println("size: %s".formatted(names.size()));   // 3
        names.remove("Bob");
        System.out.printf("contains(\"Alex\"): %s%n", names.contains("Alex"));
        System.out.println(names);

        // ages
        Map<String, Integer> ages = new HashMap<>();
        ages.put("Alex", 30);
        ages.put("Bob", 25);
        ages.get("Alex");                    // 30
        ages.get("Nobody");                  // null, исключения нет
        ages.getOrDefault("Nobody", 0);      // 0
        ages.containsKey("Bob");
        System.out.println(ages);

        for (Map.Entry<String, Integer> e : ages.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }

        // Аналог LINQ:
        // C#: names.Where(n => n.Length > 3).Select(n => n.ToUpper()).ToList()
        List<String> res = names.stream()
                .filter(n -> n.length() > 3)
                .map(String::toUpperCase)
                .toList();
    }
}
