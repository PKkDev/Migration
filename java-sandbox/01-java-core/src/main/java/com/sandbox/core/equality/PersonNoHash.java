package com.sandbox.core.equality;

import java.util.Objects;

public class PersonNoHash {
    private final String name;
    private final int age;

    PersonNoHash(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PersonNoHash p)) return false;
        return Objects.equals(name, p.name) && age == p.age;
    }
}
