package com.zeyadrashed;

import com.zeyadrashed.obj.Transaction;
import com.zeyadrashed.obj.TransactionType;
import com.zeyadrashed.util.CSVParser;
import com.zeyadrashed.util.UtilLogger;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Main business logic.
 *
 * <p>21:198:435:25SP Adv. Data Structures & Algorithms</p>
 * <p>Chapter 6 Project</p>
 * <p>Rutgers ID: 199009651</p>
 * <br>
 * <p>
 * Using a Deque ensures that when we partially consume a BUY transaction,
 * the remaining shares remain at the front of the queue (preserving FIFO order).
 *
 * @author Zeyad "zmr15" Rashed
 * @mailto zmr15@scarletmail.rutgers.edu
 * @created 31 Jan 2025
 */
public class CapitalCalculator {

    private static final DecimalFormat df = new DecimalFormat("#.##");
    private Map<String, Deque<Transaction>> purchaseQueues;
    private Map<String, Double> symbolGains;

    /**
     * Constructor to initialize the purchase queue and symbol gains maps.
     */
    public CapitalCalculator() {
        purchaseQueues = new HashMap<>();
        symbolGains = new HashMap<>();
        UtilLogger.logDebug("Initialized 'purchaseQueues' and 'symbolGains' maps in CapitalCalculator constructor");
    }

    public static void main(String[] args) {
        CapitalCalculator calculator = new CapitalCalculator();

        File csvDir = new File("csv");
        if (!csvDir.exists() || !csvDir.isDirectory()) {
            String msg = "'csv' directory not found.";
            System.err.println(msg);
            UtilLogger.logDebug(msg);
            return;
        }

        File[] csvFiles = csvDir.listFiles((_, name) -> name.toLowerCase().endsWith(".csv"));
        if (csvFiles == null || csvFiles.length == 0) {
            String msg = "No '.csv' files found in csv directory.";
            System.out.println(msg);
            UtilLogger.logDebug(msg);
            return;
        }

        for (File file : csvFiles) {
            try {
                List<Transaction> transactions = CSVParser.parseCSV(file.getAbsolutePath());
                String msg = "processing file: " + file.getName();
                System.out.println(msg);
                UtilLogger.logInfo(msg);
                calculator.processTransactions(transactions);
            } catch (Exception e) {
                String msg = "error processing file: " + file.getName() + " - ";
                System.err.println(msg + e.getMessage());
                UtilLogger.logError(msg, e);
            }
        }
        calculator.printSummary();
    }

    /**
     * Processes a single transaction.
     *
     * @param transaction the transaction to process
     */
    public void processTransaction(Transaction transaction) {
        UtilLogger.logInfo("processing transaction: " + transaction);

        if (transaction.getType() == TransactionType.BUY) {
            purchaseQueues.putIfAbsent(transaction.getSymbol(), new LinkedList<>());
            UtilLogger.logDebug("adding BUY transaction for symbol " + transaction.getSymbol());
            purchaseQueues.get(transaction.getSymbol()).addLast(transaction);
        } else if (transaction.getType() == TransactionType.SELL) {
            Deque<Transaction> buyQueue = purchaseQueues.get(transaction.getSymbol());
            int totalAvailableShares = 0;
            if (buyQueue != null) {
                for (Transaction buy : buyQueue) {
                    totalAvailableShares += buy.getQuantity();
                }
            }
            if (totalAvailableShares < transaction.getQuantity()) {
                String msg = "insufficient shares available to sell for symbol: " + transaction.getSymbol() +
                        ". Available: " + totalAvailableShares + ", Attempted to sell: " + transaction.getQuantity();
                UtilLogger.logError(msg, new IllegalStateException(msg));
                return;
            }
            UtilLogger.logDebug("processing SELL transaction for symbol " + transaction.getSymbol());
            double gain = processSellTransaction(transaction);
            String msg = "capital gain/loss for selling " + transaction.getQuantity() + " shares of " +
                    transaction.getSymbol() + ": $" + df.format(gain);
            System.out.println(msg);
            UtilLogger.logInfo(msg);
            symbolGains.put(
                    transaction.getSymbol(),
                    symbolGains.getOrDefault(transaction.getSymbol(), 0.0) + gain
            );
        }
    }

    /**
     * Processes a sell transaction.
     *
     * @param sellTransaction the sell transaction
     * @return the calculated capital gain (or loss)
     */
    private double processSellTransaction(Transaction sellTransaction) {
        UtilLogger.logDebug("begin processing sell transaction: " + sellTransaction);

        Deque<Transaction> buyQueue = purchaseQueues.get(sellTransaction.getSymbol());
        int sharesToSell = sellTransaction.getQuantity();
        double totalCostBasis = 0.0;

        while (sharesToSell > 0) {
            Transaction buyTransaction = buyQueue.peekFirst();
            if (buyTransaction == null) {
                String msg = "unexpected error: no BUY transaction available for symbol: " + sellTransaction.getSymbol();
                UtilLogger.logError(msg, new IllegalStateException(msg));
                return 0.0;
            }

            int availableShares = buyTransaction.getQuantity();

            if (availableShares <= sharesToSell) {
                UtilLogger.logDebug("consuming entire BUY transaction: " + buyTransaction + " for " + availableShares + " shares");
                totalCostBasis += availableShares * buyTransaction.getPrice();
                sharesToSell -= availableShares;
                buyQueue.pollFirst();
            } else {
                UtilLogger.logDebug("partially consuming BUY transaction: " + buyTransaction + ". using " + sharesToSell + " shares");
                totalCostBasis += sharesToSell * buyTransaction.getPrice();
                int remainingShares = availableShares - sharesToSell;
                Transaction updatedBuy = new Transaction(
                        buyTransaction.getDate(),
                        buyTransaction.getType(),
                        buyTransaction.getSymbol(),
                        remainingShares,
                        buyTransaction.getPrice()
                );
                buyQueue.pollFirst();
                buyQueue.addFirst(updatedBuy);
                sharesToSell = 0;
            }
        }
        double totalProceeds = sellTransaction.getQuantity() * sellTransaction.getPrice();
        UtilLogger.logDebug("completed processing sell transaction: " + sellTransaction);
        return totalProceeds - totalCostBasis;
    }

    /**
     * Processes a list of transactions.
     *
     * @param transactions the list of transactions to process
     */
    public void processTransactions(List<Transaction> transactions) {
        UtilLogger.logInfo("processing " + transactions.size() + " transactions");
        for (Transaction t : transactions) {
            processTransaction(t);
        }
        UtilLogger.logInfo("completed processing transactions list");
    }

    /**
     * Prints a summary of the aggregated capital gains/losses per stock symbol.
     */
    public void printSummary() {
        UtilLogger.logInfo("Printing summary of capital gains/losses per stock symbol:");
        System.out.println("\n--- Capital Gains/Losses Summary ---");
        for (Map.Entry<String, Double> entry : symbolGains.entrySet()) {
            String symbol = entry.getKey();
            double totalGain = entry.getValue();
            String msg = symbol + ": $" + df.format(totalGain);
            System.out.println(msg);
            UtilLogger.logInfo(msg);
        }
        System.out.println("-------------------------------------\n");
    }
}