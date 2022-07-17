package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.model.Account;

public interface IAccountsRepository {
    boolean saveAccount(Account a);
    Account getAccount(String accountNumber);
}
