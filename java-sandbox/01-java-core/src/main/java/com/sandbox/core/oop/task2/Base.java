package com.sandbox.core.oop.task2;

public class Base {
    public Base() {
        hello();
    }

    public void hello() {
        System.out.println("Hello from Base");
    }

    public static Base create() {
        System.out.println("Base create");
        return new Base();
    }
}