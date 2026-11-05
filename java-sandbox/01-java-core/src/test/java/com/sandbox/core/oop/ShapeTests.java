package com.sandbox.core.oop;

import com.sandbox.core.oop.task1.Circle;
import com.sandbox.core.oop.task1.Rectangle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShapeTests {
    @Test
    void correctCalcRectangleArea() {
        var rectangle = new Rectangle(1, 2, "rectangle");
        assertEquals(2, rectangle.area());
    }

    @Test
    void correctCalcCircleArea() {
        var circle = new Circle(3, "circle");
        assertEquals(28.274333882308138, circle.area(), 1e-9);
    }
}
