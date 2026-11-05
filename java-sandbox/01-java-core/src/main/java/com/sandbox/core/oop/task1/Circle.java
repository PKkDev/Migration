package com.sandbox.core.oop.task1;

public class Circle extends Shape implements Printable {
    private final double radius;

    public Circle(double radius, String name) {
        super(name);
        this.radius = radius;
    }

    @Override
    public void print() {
        System.out.println("area: %s".formatted(area()));
    }

    @Override
    public double area() {
        return Math.PI * Math.pow(radius, 2);
    }
}
