package com.sandbox.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

public class EqualityTest {

    @Test
    void equalsOperations() {
        Integer z1 = 127;
        long z2 = 127L;
        assertTrue(z1 == z2);
        assertFalse(z1.equals(z2));

        Integer z5 = 128; // => Integer a = Integer.valueOf(128);
        Integer z6 = 128; // => Integer a = Integer.valueOf(128);
        assertFalse(z5 == z6);
        assertTrue(z5.equals(z6));
    }

    @Test
    void addExactWithMax() {
        Executable fn = () -> {
            var intMaxPlusOne2 = Math.addExact(Integer.MAX_VALUE, 1);
        };
        assertThrows(ArithmeticException.class, fn);
    }

    @Test
    void checkSameCache() {
        assertSame(Integer.valueOf(127), Integer.valueOf(127));
    }

    @Test
    void checkNotSameCache() {
        assertNotSame(Integer.valueOf(128), Integer.valueOf(128));
    }
}
