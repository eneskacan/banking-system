package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.model.Account;

public interface IAccountsRepository {
    Account createAccount(Account account);
    Account updateAccount(Account account);
    Account getAccount(long id);
    boolean deleteAccount(Account account);
}
