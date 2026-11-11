package com.sandbox.core.generics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.sandbox.core.generics.GenericsDemo.genericParse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class GenericsTests {
    @Test
    void testGenericParseOk() {
        var result = genericParse("55");
        assertInstanceOf(GenericsDemo.Ok.class, result);
    }

    @Test
    void testGenericParseErr() {
        var result = genericParse("55kk");
        var check = result instanceof GenericsDemo.Err<Integer>;
        Assertions.assertTrue(check);
    }
}
