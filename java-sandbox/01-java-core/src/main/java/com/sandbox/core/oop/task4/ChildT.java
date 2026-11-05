package com.sandbox.core.oop.task4;

public class ChildT extends ParentT {

    public String name = "ChildT";

    public void say() {
        System.out.println("%s say".formatted("ChildT"));
    }
}
