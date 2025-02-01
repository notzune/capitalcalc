package com.zeyadrashed.util;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Util class for building log files for bug testing/error handling.
 *
 * <p>21:198:435:25SP Adv. Data Structures & Algorithms</p>
 * <p>Chapter 6 Project</p>
 * <p>Rutgers ID: 199009651</p>
 * <br>
 *
 * @author Zeyad "zmr15" Rashed
 * @mailto zmr15@scarletmail.rutgers.edu
 * @created 01 Feb 2025
 */
public class UtilLogger {

    private static final StringBuilder logBuffer = new StringBuilder();
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Logs an informational message.
     *
     * @param message the info message to log
     */
    public static void logInfo(String message) {
        String timestamp = LocalDateTime.now().format(dtf);
        logBuffer.append("INFO  [")
                .append(timestamp)
                .append("] ")
                .append(message)
                .append(System.lineSeparator());
    }

    /**
     * Logs an error message along with the stack trace of an exception.
     *
     * @param message the error message to log
     * @param e       the exception to log
     */
    public static void logError(String message, Exception e) {
        String timestamp = LocalDateTime.now().format(dtf);
        logBuffer.append("ERROR [")
                .append(timestamp)
                .append("] ")
                .append(message)
                .append(System.lineSeparator());
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        logBuffer.append(exceptionAsString)
                .append(System.lineSeparator());
    }

    /**
     * Logs a debug message.
     *
     * @param message the debug message to log
     */
    public static void logDebug(String message) {
        String timestamp = LocalDateTime.now().format(dtf);
        logBuffer.append("DEBUG [")
                .append(timestamp)
                .append("] ")
                .append(message)
                .append(System.lineSeparator());
    }

    /**
     * Logs a warning message.
     *
     * @param message the debug message to log
     */
    public static void logWarning(String message) {
        String timestamp = LocalDateTime.now().format(dtf);
        logBuffer.append("WARN [")
                .append(timestamp)
                .append("] ")
                .append(message)
                .append(System.lineSeparator());
    }

    /**
     * Exports the current log buffer content to a file.
     *
     * @param filePath the path where the log file will be written
     * @throws IOException if an error occurs while writing to the file
     */
    public static void exportLog(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(logBuffer.toString());
        }
    }

    /**
     * Clears the log buffer.
     */
    public static void clearLog() {
        logBuffer.setLength(0);
    }

    /**
     * Returns the entire log as a single string.
     *
     * @return String
     */
    public static String getLog() {
        return logBuffer.toString();
    }
}