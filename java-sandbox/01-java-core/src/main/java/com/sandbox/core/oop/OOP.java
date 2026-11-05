package com.sandbox.core.oop;

import com.sandbox.core.oop.task1.Circle;
import com.sandbox.core.oop.task1.Printable;
import com.sandbox.core.oop.task1.Rectangle;
import com.sandbox.core.oop.task1.Shape;
import com.sandbox.core.oop.task2.Base;
import com.sandbox.core.oop.task2.Child;
import com.sandbox.core.oop.task3.AB;
import com.sandbox.core.oop.task4.ChildT;
import com.sandbox.core.oop.task4.ParentT;
import com.sandbox.core.oop.task5.InitChild;
import com.sandbox.core.oop.task5.InitParent;

import java.util.ArrayList;
import java.util.List;

public class OOP {
    static void main() {
        List<Shape> shapes = new ArrayList<>();
        shapes.add(new Rectangle(15, 30, "Rectangle1"));
        shapes.add(new Circle(25, "Circle1"));
        shapes.add(new Rectangle(2, 23, "Rectangle2"));
        shapes.add(new Circle(16, "Circle2"));

        for (Shape shape : shapes) {
            System.out.println(shape);
        }

        System.out.println("----");

        for (Shape shape : shapes) {
            System.out.print("shape print 1: ");
            ((Printable) shape).print();
            System.out.print("");
        }

        System.out.println("----");

        for (Shape shape : shapes) {
            if (shape instanceof Printable printable) {
                System.out.print("shape print 2: ");
                printable.print();
                System.out.print("");
            }
        }

        System.out.println("----");

        for (Shape shape : shapes) {
            if (shape instanceof Printable printable) {
                System.out.print("shape printTwice: ");
                printable.printTwice();
                System.out.print("");
            }
        }

        System.out.println("----");

        Base base1 = new Base();
        Child child1 = new Child();

        // Выведет null т.к.
        // Вызывая new Child мы вызываем new Base, а он вызывает hello() в конструкторе
        // Но вызывает он hello(), который переопределён в Child, который печатает name, которого на этот момент ещё нет

        // Если сделать его final в Base, то его нельзя будет переопределить в child

        // Если убрать вызов из конструктора Base, то в консоль ничего не выведется, но и ошибки не будет

        System.out.println("----");

        var a1 = Base.create(); // Base
        var a2 = Child.create(); // Base Child

        System.out.println("----");

        var ab = new AB();
        ab.who();

        System.out.println("----");

        ParentT p1 = new ChildT();
        p1.say(); // ChildT say
        System.out.println("name %s".formatted(p1.name)); // ChildT

        ParentT p2 = new ParentT();
        System.out.println("name %s".formatted(p2.name)); // ParentT
        p2.say(); // ParentT say

        System.out.println("----");

        new InitParent();
        System.out.println("----");
        new InitParent();
        System.out.println("----");
        new InitChild();
    }
}
