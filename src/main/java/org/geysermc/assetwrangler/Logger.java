package org.geysermc.assetwrangler;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BuildConstants.getInstance().getName());

    public static void debug(String text) {
        LOGGER.debug(text);
    }

    public static void debug(String text, Throwable t) {
        LOGGER.debug(text, t);
    }

    public static void info(String text) {
        LOGGER.info(text);
    }

    public static void info(String text, Throwable t) {
        LOGGER.info(text, t);
    }

    public static void warn(String text) {
        LOGGER.warn(text);
    }

    public static void warn(String text, Throwable t) {
        LOGGER.warn(text, t);
    }

    public static void warnWithDialog(String text, @Nullable Throwable t, @Nullable Component parent) {
        JOptionPane.showMessageDialog(parent, text + exceptionToString(t), "uh oh", JOptionPane.WARNING_MESSAGE);

        if (t == null) warn(text);
        else warn(text, t);
    }

    public static void error(String text) {
        LOGGER.error(text);
    }

    public static void error(String text, Throwable t) {
        LOGGER.error(text, t);
    }

    public static void errorWithDialog(String text, @Nullable Throwable t, @Nullable Component parent) {
        JOptionPane.showMessageDialog(parent, text + exceptionToString(t), "Error! Error!", JOptionPane.ERROR_MESSAGE);

        if (t == null) error(text);
        else error(text, t);
    }

    private static String exceptionToString(@Nullable Throwable t) {
        if (t == null) return "";

        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        t.printStackTrace(printWriter);
        return "\n" + writer;
    }
}
