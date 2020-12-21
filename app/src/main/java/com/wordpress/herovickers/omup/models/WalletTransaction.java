package com.wordpress.herovickers.omup.models;

public class WalletTransaction {
    Double amount;
    long createdAt;
    String transactionType;

    public WalletTransaction(Double amount, long createdAt, String transactionType) {
        this.amount = amount;
        this.createdAt = createdAt;
        this.transactionType = transactionType;
    }

    public WalletTransaction() {
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getAmount() {
        return amount;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getTransactionType() {
        return transactionType;
    }
}
