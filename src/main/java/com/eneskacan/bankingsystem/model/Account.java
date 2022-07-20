package com.eneskacan.bankingsystem.model;

import lombok.*;

@Data
@Builder
public class Account {

    private long id;
    private final String name;
    private final String surname;
    private final String email;
    private final String idNumber;
    private final AssetTypes accountType;
    private long lastUpdated;
    private int isDeleted;

    @Setter(AccessLevel.NONE)
    private double balance = 0;

    public boolean deposit(double amount) {
        // Update balance
        this.balance += amount;

        return true;
    }

    public boolean withdraw(double amount) {
        // Insufficient funds
        if(this.balance < amount) return false;

        // Update balance
        this.balance -= amount;

        return true;
    }
}
