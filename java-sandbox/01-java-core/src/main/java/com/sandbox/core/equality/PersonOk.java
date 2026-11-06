package com.sandbox.core.equality;

import java.util.Objects;

public class PersonOk {
    private final String name;
    private final int age;

    public PersonOk(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PersonOk p)) return false;
        return Objects.equals(name, p.name) && age == p.age;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}
