package com.zeyadrashed.obj;

import java.time.LocalDate;

/**
 * Object representing a transaction/trade.
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
public class Transaction {

    private LocalDate date;
    private TransactionType type;
    private String symbol;
    private int quantity;
    private double price;

    /**
     * Constructor for a stock transaction.
     *
     * @param date     the date of the transaction
     * @param type     the transaction type (BUY or SELL)
     * @param symbol   the stock symbol
     * @param quantity the number of shares
     * @param price    the price per share
     */
    public Transaction(LocalDate date, TransactionType type, String symbol, int quantity, double price) {
        this.date = date;
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
    }

    /**
     * Gets the transaction date.
     *
     * @return transaction date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Gets the transaction type.
     *
     * @return TransactionType
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Gets the stock symbol.
     *
     * @return symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Gets the quantity of shares.
     *
     * @return number of shares
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Gets the price per share.
     *
     * @return price per share
     */
    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("Transaction[date=%s, type=%s, symbol=%s, quantity=%d, price=%.2f]",
                date, type, symbol, quantity, price);
    }
}
