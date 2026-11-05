package com.sandbox.core.oop.task2;

public class Child extends Base {
    private String name = "Alex";

    public static Child create() {
        System.out.println("Child create");
        return new Child();
    }

    @Override
    public void hello() {
        System.out.println("Hello from Child " + name);
    }
}
