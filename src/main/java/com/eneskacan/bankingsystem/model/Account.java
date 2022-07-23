package com.eneskacan.bankingsystem.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Data
@Builder
@Table("accounts")
public class Account {
    @Id
    private long id;
    private final String name;
    private final String surname;
    private final String email;
    private final String idNumber;
    private final AssetTypes accountType;
    @Setter(AccessLevel.NONE)
    private double balance;
    private Timestamp lastUpdated;
    private boolean isDeleted;

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
