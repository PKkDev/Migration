package com.sandbox.core.oop.task1;

public interface Printable {
    void print();

    default void printTwice() {
        print();
        print();
    }
}
