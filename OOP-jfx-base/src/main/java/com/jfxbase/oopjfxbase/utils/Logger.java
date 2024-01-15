package com.jfxbase.oopjfxbase.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Logger {
    private final static DateFormat errorDateFormat = new SimpleDateFormat("HH:mm:ss");

    public static void info(String infoMessage) {
        log("INFO", infoMessage, null);
    }

    public static void error(String errorMessage) {
        log("ERROR", errorMessage, null);
    }

    public static void error(String errorMessage, Exception exception) {
        log("ERROR", errorMessage, exception);
    }

    private static void log(String level, String message, Exception exception) {
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName(); // Adjusted index to get the correct method name
        String formattedMessage = String.format("-- %s -- [%s][%s]: %s%n", level, errorDateFormat.format(new Date()), methodName, message);

        if (exception != null) {
            formattedMessage += Arrays.toString(exception.getStackTrace());
        }

        if ("ERROR".equals(level)) {
            System.err.println(formattedMessage);
        } else {
            System.out.println(formattedMessage);
        }
    }
}
