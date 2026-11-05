package com.sandbox.core.oop.task1;

public abstract class Shape {
    private final String name;

    protected Shape(String name) {
        this.name = name;
    }

    public abstract double area();

    @Override
    public String toString() {
        return "class '%s' with name '%s'".formatted(getClass().getSimpleName(), name);
    }
}
