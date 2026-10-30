package com.sandbox.core;

public class ConsoleColor {
    public static final String RED_BRIGHT = "\033[0;91m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static void redLog(String x) {
        System.out.println(RED_BRIGHT + x + ANSI_RESET);
    }
}
