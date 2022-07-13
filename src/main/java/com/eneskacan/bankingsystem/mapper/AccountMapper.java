package com.eneskacan.bankingsystem.mapper;

import com.eneskacan.bankingsystem.dto.generic.AccountDTO;
import com.eneskacan.bankingsystem.model.Account;
import com.eneskacan.bankingsystem.dto.request.AccountCreationRequest;
import com.eneskacan.bankingsystem.model.AssetTypes;

import java.util.Locale;

public final class AccountMapper {

    private AccountMapper() {
        throw new IllegalStateException("Utility class");
    }

    // Transforms coma separated values to Account object
    public static Account toAccount(String csv) {
        String[] values = csv.split(",");

        return Account.builder()
                .accountNumber(values[0])
                .name(values[1])
                .surname(values[2])
                .email(values[3])
                .idNumber(values[4])
                .accountType(AssetTypes.valueOf(values[5]))
                .balance(Double.parseDouble(values[6]))
                .lastUpdated(Long.parseLong(values[7]))
                .build();
    }

    // Transforms Account Creation Request to Account object
    public static Account toAccount(AccountCreationRequest request) {
        return Account.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .idNumber(request.getIdNumber())
                .accountType(AssetTypes.valueOf(request.getType().toUpperCase(Locale.ROOT)))
                .build();
    }

    // Transforms Account object to Account DTO
    public static AccountDTO toDto(Account a) {
        return AccountDTO.builder()
                .accountNumber(a.getAccountNumber())
                .name(a.getName())
                .surname(a.getSurname())
                .email(a.getEmail())
                .idNumber(a.getIdNumber())
                .accountType(a.getAccountType())
                .balance(a.getBalance())
                .lastUpdated(a.getLastUpdated())
                .build();
    }

    // Transforms Account objects to coma separated values
    public static String toCsv(Account a) {
        return String.format("%s,%s,%s,%s,%s,%s,%.3f,%s%n", a.getAccountNumber(), a.getName(), a.getSurname(),
                a.getEmail(), a.getIdNumber(), a.getAccountType(), a.getBalance(), a.getLastUpdated());
    }
}