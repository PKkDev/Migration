package com.sandbox.core.exceptions;

import java.io.IOException;

public class Res implements AutoCloseable {
    private final String name;

    public Res(String name) {
        this.name = name;
        System.out.printf("open %s%n", name);
    }

    @Override
    public void close() throws IOException {
        System.out.printf("close %s%n", name);
        throw new IOException("close-fail %s".formatted(name));

    }
}
