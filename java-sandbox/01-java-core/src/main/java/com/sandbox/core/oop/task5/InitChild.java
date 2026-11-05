package com.sandbox.core.oop.task5;

public class InitChild extends InitParent {
    static {
        System.out.println("Child: статический блок");
    }

    private String f = log("Child: поле");

    {
        System.out.println("Child: блок экземпляра");
    }

    public InitChild() {
        System.out.println("Child: конструктор");
    }

    static String log(String s) {
        System.out.println(s);
        return s;
    }   // помощник, чтобы поле тоже «писало» в консоль
}
