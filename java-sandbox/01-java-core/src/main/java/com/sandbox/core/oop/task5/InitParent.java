package com.sandbox.core.oop.task5;

public class InitParent {
    static {
        System.out.println("Parent: статический блок");
    }

    private String f = log("Parent: поле");

    {
        System.out.println("Parent: блок экземпляра");
    }

    public InitParent() {
        System.out.println("Parent: конструктор");
    }

    static String log(String s) {
        System.out.println(s);
        return s;
    }   // помощник, чтобы поле тоже «писало» в консоль
}
