package com.zeyadrashed.util;

import com.zeyadrashed.obj.TransactionType;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Utility class that generates a random CSV file with a given number of transactions.
 * The generated csv will have columns: date,transactionType,symbol,quantity,price
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
public class CSVGenerator {

    private static final String[] SYMBOLS = {"AAPL", "GOOG", "MSFT", "AMZN"};
    private static final Random random = new Random();
    private static final String sysDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

    /**
     * Generates a random CSV file with a given number of transactions.
     *
     * @param filePath        the path where the CSV file will be written
     * @param numTransactions the number of transactions to generate
     * @throws IOException if there is an error writing the file
     */
    public static void generateCSV(String filePath, int numTransactions) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("date,transactionType,symbol,quantity,price");
            bw.newLine();

            LocalDate startDate = LocalDate.of(2020, 1, 1);
            UtilLogger.logDebug("generating test dataset (" + sysDate + "_sample_transactions.csv file)....");

            for (int i = 0; i < numTransactions; i++) {
                LocalDate date = startDate.plusDays(random.nextInt(5 * 365));
                TransactionType type = random.nextBoolean() ? TransactionType.BUY : TransactionType.SELL;
                String symbol = SYMBOLS[random.nextInt(SYMBOLS.length)];
                int quantity = random.nextInt(200) + 1;
                double price = 10 + (490 * random.nextDouble());

                String line = String.format("%s,%s,%s,%d,%.2f", date, type, symbol, quantity, price);
                bw.write(line);
                bw.newLine();

                UtilLogger.logDebug("generated line: " + line);
            }
        }
    }

    /**
     * Test method for testing CSV generation.
     */
    @Test
    public void testGenerator() {
        try {
            generateCSV("csv/" + sysDate + "_sample_transactions.csv", 10);
            System.out.println("CSV file generated successfully.");
            UtilLogger.logInfo("CSV file generated successfully.");
        } catch (IOException e) {
            System.err.println("error generating csv: " + e.getMessage());
        }
    }
}