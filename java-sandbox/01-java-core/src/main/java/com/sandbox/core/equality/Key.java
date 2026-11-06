package com.sandbox.core.equality;

import java.util.Objects;

public class Key {
    String id;                       // не final: так задумано

    Key(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Key p)) return false;
        return Objects.equals(id, p.id);
    }
}
