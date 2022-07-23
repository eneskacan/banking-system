package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.model.Account;
import com.eneskacan.bankingsystem.mybatis.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("MyBatisAccountsRepository")
public class MyBatisAccountsRepository implements IAccountsRepository {

    private final AccountMapper accountMapper;

    @Autowired
    public MyBatisAccountsRepository(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Override
    public Account createAccount(Account account) {
        accountMapper.save(account);
        return account;
    }

    @Override
    public Account updateAccount(Account account) {
        if(accountMapper.update(account)) {
            return account;
        }
        return null;
    }

    @Override
    public Account getAccount(long id) {
        return accountMapper.findById(id);
    }

    @Override
    public boolean deleteAccount(Account account) {
        account.setDeleted(true); // set deleted flag as true
        Account updatedAccount = updateAccount(account);

        return updatedAccount != null && updatedAccount.isDeleted();
    }
}
