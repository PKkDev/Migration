package com.sandbox.core.strings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class TextStatsTest {
    // случаи: `""` → `0`/`0`, `"abc"` → `3`/`3`, `"Привет"` → `6`/`6`, `"😀"` → кодовых точек `1`, `length()` равно `2`, графем `1`, `"👨‍👩‍👧"` → `5`/`1`.

    @ParameterizedTest
    @CsvSource({"'',  0,  0", "abc, 3,  3", "Привет,  6,  6"})
    void correctStringLength(String str, int expected1, int expected2) {
        assertEquals(expected1, str.length());
        assertEquals(expected2, str.codePointCount(0, str.length()));
    }


    @Test
    void correctStringLengthSpec1() {
        var str = "🌍";
        assertEquals(2, str.length());
        assertEquals(1, str.codePointCount(0, str.length()));
    }

    @Test
    void correctStringLengthSpec2() {
        var str = "😀";
        assertEquals(2, str.length());
        assertEquals(1, str.codePointCount(0, str.length()));
    }

    @Test
    void correctStringLengthSpec3() {
        var str = "👨‍👩‍👧";
        assertEquals(8, str.length());
        assertEquals(5, str.codePointCount(0, str.length()));
    }
}
