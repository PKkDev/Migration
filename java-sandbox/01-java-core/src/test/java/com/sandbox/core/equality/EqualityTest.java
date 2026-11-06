package com.sandbox.core.equality;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class EqualityTest {
    @Test
    void setWithoutHashCodeKeepsDuplicates() {
        Set<PersonNoHash> set1 = new HashSet<>();
        var a1 = new PersonNoHash("Alex", 30);
        var a2 = new PersonNoHash("Alex", 30);
        set1.add(a1);
        set1.add(a2);
        System.out.println("equals: " + a1.equals(a2)); // true а где тут баг?
        System.out.println("size: " + set1.size()); // 2

        Assertions.assertEquals(true, a1.equals(a2));
        Assertions.assertEquals(2, set1.size());
    }

    @Test
    void sortByAgeThenName() {

        List<Person> people = new ArrayList<>(List.of(
                new Person("Bob", 30),
                new Person("Alex", 30),
                new Person("Carl", 25)
        ));

        people.sort(Comparator.comparing(Person::age).thenComparing(Person::name));

        List<Person> expectedResult = new ArrayList<>(List.of(new Person("Carl", 25), new Person("Alex", 30), new Person("Bob", 30)));
        Assertions.assertEquals(expectedResult, people);
    }

    @Test
    void missedKey() {
        Map<Key, String> map = new HashMap<>();
        Key key = new Key("a");
        map.put(key, "value");

        Assertions.assertNotNull(map.get(key));
        Assertions.assertTrue(map.containsKey(key));

        key.id = "b";

        Assertions.assertNull(map.get(key));
        Assertions.assertFalse(map.containsKey(key));
        Assertions.assertNull(map.get(new Key("a")));
        Assertions.assertEquals(1, map.size());
    }
}
