package com.sandbox.core.equality;

import java.util.*;

public class EqualityDemo {
    static void main() {
        Set<PersonNoHash> set1 = new HashSet<>();
        var a1 = new PersonNoHash("Alex", 30);
        var a2 = new PersonNoHash("Alex", 30);
        set1.add(a1);
        set1.add(a2);
        System.out.println("equals: " + a1.equals(a2)); // true
        System.out.println("size: " + set1.size()); // 2
        // HashSet - коллекция уникальных элементов
        // получилось что 2 объекта равны по equals, но в HashSet всё равно 2 объекта т.к. у них разные hashcode

        System.out.println("-----");

        Set<PersonOk> set2 = new HashSet<>();
        var a3 = new PersonOk("Alex", 30);
        var a4 = new PersonOk("Alex", 30);
        set2.add(a3);
        set2.add(a4);
        System.out.println("equals: " + a3.equals(a4)); // true
        System.out.println("size: " + set2.size()); // 1

        System.out.println("-----");

        Map<Key, String> map = new HashMap<>();
        Key key = new Key("a");
        map.put(key, "value");

        System.out.println("до:");
        System.out.println("get(key): " + map.get(key)); // value
        System.out.println("containsKey: " + map.containsKey(key)); // true

        key.id = "b";

        System.out.println("после:");
        System.out.println("get(key): " + map.get(key));  // null
        System.out.println("containsKey: " + map.containsKey(key)); // false
        System.out.println("get(new Key(\"a\")): " + map.get(new Key("a"))); // null
        System.out.println("size: " + map.size()); // 1
        System.out.println("values: " + map.values()); // [value]

        // HashMap - словарь «ключ — значение»
        // и т.к. мы изменяем свойство id, а оно используется в получение hashCode, а HashMap как ключ использует hashCode,
        // а он теперь другой и мы работаем как бы с новым объектом

        // 1. По хешу выбирает корзину.
        // При put запись легла в корзину для хеша "a" и вместе с ней сохранился хеш "a".
        // Хеш new Key("a") совпадает с ним, поэтому корзина найдена верно.
        // 2. Внутри корзины сравнивает ключи через equals.
        // У записи лежит тот же объект key, но у него теперь id = "b".
        // Вызывается new Key("a").equals(key) → "a" против "b" → false. Запись не подходит.

        System.out.println("-----");

        List<Person> people = new ArrayList<>(List.of(
                new Person("Bob", 30),
                new Person("Alex", 30),
                new Person("Carl", 25)
        ));

        // по возрастанию age -> по возрастанию name
        people.sort(Comparator.comparing(Person::age).thenComparing(Person::name)); // Carl Alex Bob
        System.out.println(people);

        // по убыванию age -> по возрастанию name
        people.sort(Comparator.comparing(Person::age).reversed().thenComparing(Person::name)); // Alex Bob Carl
        System.out.println(people);


        // Что гарантирует контракт между equals и hashCode, и что произойдёт, если нарушить его?
        // Равные по equals объекты обязаны иметь равные hashCode.
        // Если нарушить (например, переопределить только equals), HashSet хранит дубли, а HashMap не находит ключи по равному объекту

        // Почему запись в HashMap «теряется» при изменении ключа?
        // Сначала ищем корзину по hashCode, а внутри корзины сравниваем ключи через equals
        // hashCode одинаковый, а equals разный - "мёртвая" запись

        // Чем Comparable отличается от Comparator, и когда нужен каждый?
        // Comparable - интерфейс для сравнения через метод внутри класса
        // Comparator - универсальная утилита для сравнения объектов через делегаты

        // Почему нельзя сравнивать числа через вычитание в compareTo?
        // Риск переполнения

        // Чем Object.hashCode по умолчанию отличается от переопределённого?
        // В самом способе вычисления hashCode - встроенный в JVM (не учитывает состояние объекта) и или определённый нами
    }
}
