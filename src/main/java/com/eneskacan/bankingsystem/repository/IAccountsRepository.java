package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.exception.UnexpectedErrorException;
import com.eneskacan.bankingsystem.model.Account;

import javax.security.auth.login.AccountNotFoundException;

public interface IAccountsRepository {
    Account createAccount(Account account) throws UnexpectedErrorException;
    Account updateAccount(Account account) throws UnexpectedErrorException;
    Account getAccount(long id) throws UnexpectedErrorException, AccountNotFoundException;
    boolean deleteAccount(Account account) throws UnexpectedErrorException;
}
