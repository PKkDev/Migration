package com.sandbox.core.common;

/// https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
public class ConsoleColor {
    public static final String RED_BRIGHT = "\033[0;91m";
    public static final String GREEN_BRIGHT = "\033[0;92m";

    public static final String ANSI_RESET = "\u001B[0m";

    public static void redLog(String x) {
        System.out.println(RED_BRIGHT + x + ANSI_RESET);
    }

    public static void greenLog(String x) {
        System.out.println(GREEN_BRIGHT + x + ANSI_RESET);
    }
}
