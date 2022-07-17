package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.model.Account;

public interface IAccountsRepository {
    Account saveAccount(Account a);
    Account getAccount(String accountNumber);
}
