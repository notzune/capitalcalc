package com.zeyadrashed;

import com.zeyadrashed.obj.Transaction;
import com.zeyadrashed.obj.TransactionType;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * Main business logic.
 *
 * <p>21:198:435:25SP Adv. Data Structures & Algorithms</p>
 * <p>Chapter 6 Project</p>
 * <p>Rutgers ID: 199009651</p>
 * <br>
 *
 * @author Zeyad "zmr15" Rashed
 * @mailto zmr15@scarletmail.rutgers.edu
 * @created 31 Jan 2025
 */
public class CapitalCalculator {

    private Map<String, Queue<Transaction>> purchaseQueues;
    private static final DecimalFormat df = new DecimalFormat("#.##");

    /**
     * Constructor to initialize the purchase queue map.
     */
    public CapitalCalculator() {
        purchaseQueues = new HashMap<>();
    }

    /**
     * Processes a single transaction.
     *
     * @param transaction the transaction to process
     */
    public void processTransaction(Transaction transaction) {
        // if the transaction is a BUY, add it to the queue
        if (transaction.getType() == TransactionType.BUY) {
            // if there is no queue for the symbol, create one
            purchaseQueues.putIfAbsent(transaction.getSymbol(), new LinkedList<>());
            // add the buy transaction to the queue
            purchaseQueues.get(transaction.getSymbol()).offer(transaction);
        } else if (transaction.getType() == TransactionType.SELL) {
            // process a sell transaction and calculate capital gain
            double gain = processSellTransaction(transaction);
            System.out.println("Capital gain/loss for selling " + transaction.getQuantity() + " shares of "
                    + transaction.getSymbol() + ": $" + df.format(gain));
        }
    }

    /**
     * Processes a sell transaction.
     *
     * @param sellTransaction the sell transaction
     * @return the calculated capital gain (or loss)
     */
    private double processSellTransaction(Transaction sellTransaction) {
        Queue<Transaction> buyQueue = purchaseQueues.get(sellTransaction.getSymbol());
        if (buyQueue == null || buyQueue.isEmpty()) {
            throw new IllegalStateException("no shares available to sell for symbol: " + sellTransaction.getSymbol());
        }

        int sharesToSell = sellTransaction.getQuantity();
        double totalCostBasis = 0.0;

        while (sharesToSell > 0) {
            Transaction buyTransaction = buyQueue.peek();
            if (buyTransaction == null) {
                throw new IllegalStateException("selling more shares than available for symbol: " + sellTransaction.getSymbol());
            }

            int availableShares = buyTransaction.getQuantity();

            if (availableShares <= sharesToSell) {
                totalCostBasis += availableShares * buyTransaction.getPrice();
                sharesToSell -= availableShares;
                buyQueue.poll();
            } else {
                totalCostBasis += sharesToSell * buyTransaction.getPrice();
                int remainingShares = availableShares - sharesToSell;
                Transaction updatedBuy = new Transaction(
                        buyTransaction.getDate(),
                        buyTransaction.getType(),
                        buyTransaction.getSymbol(),
                        remainingShares,
                        buyTransaction.getPrice()
                );
                buyQueue.poll();
                buyQueue.offer(updatedBuy);  // note: for a strict FIFO, will need a different approach to update in place.
                sharesToSell = 0;
            }
        }
        double totalProceeds = sellTransaction.getQuantity() * sellTransaction.getPrice();
        return totalProceeds - totalCostBasis;
    }

    /**
     * Processes a list of transactions.
     *
     * @param transactions the list of transactions to process
     */
    public void processTransactions(List<Transaction> transactions) {
        for (Transaction t : transactions) {
            processTransaction(t);
        }
    }

    public static void main(String[] args) {
        CapitalCalculator calculator = new CapitalCalculator();

        List<Transaction> transactions = new ArrayList<>();

        transactions.add(new Transaction(LocalDate.of(2025, 1, 1), TransactionType.BUY, "AAPL", 100, 150.00));
        transactions.add(new Transaction(LocalDate.of(2025, 2, 1), TransactionType.BUY, "AAPL", 50, 155.00));
        transactions.add(new Transaction(LocalDate.of(2025, 3, 1), TransactionType.SELL, "AAPL", 120, 160.00));

        calculator.processTransactions(transactions);
    }
}