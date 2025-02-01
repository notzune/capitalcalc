package com.zeyadrashed.util;

import com.zeyadrashed.obj.Transaction;
import com.zeyadrashed.obj.TransactionType;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Parses a CSV file into a list of transactions.
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
public class CSVParser {

    /**
     * Parses a CSV file into a list of transactions.
     *
     * @param filePath the path to the CSV file
     * @return a list of Transaction objects
     * @throws IOException if there is an error reading the file
     */
    public static List<Transaction> parseCSV(String filePath) throws IOException {
        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine && line.toLowerCase().contains("date")) {
                    isFirstLine = false;
                    continue;
                }
                isFirstLine = false;

                String[] parts = line.split(",");
                if (parts.length < 5) {
                    continue;
                }

                LocalDate date = LocalDate.parse(parts[0].trim()); // expects yyyy-mm-dd format
                TransactionType type = TransactionType.valueOf(parts[1].trim().toUpperCase());
                String symbol = parts[2].trim();
                int quantity = Integer.parseInt(parts[3].trim());
                double price = Double.parseDouble(parts[4].trim());

                Transaction transaction = new Transaction(date, type, symbol, quantity, price);
                transactions.add(transaction);
            }
        }
        return transactions;
    }
}