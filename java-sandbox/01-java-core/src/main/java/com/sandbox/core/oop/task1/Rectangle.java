package com.sandbox.core.oop.task1;

public class Rectangle extends Shape implements Printable {
    private final double width;
    private final double height;

    public Rectangle(double width, double height, String name) {
        super(name);
        this.width = width;
        this.height = height;
    }

    @Override
    public void print() {
        System.out.println("area: %s".formatted(area()));
    }

    @Override
    public double area() {
        return width * height;
    }
}
