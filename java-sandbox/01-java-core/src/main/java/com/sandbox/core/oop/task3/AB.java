package com.sandbox.core.oop.task3;

public class AB implements A, B {

    @Override
    public void who() {
        A.super.who();
        B.super.who();
    }
}
