package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("SpringJdbcAccountsRepository")
public class SpringJdbcAccountsRepository implements IAccountsRepository{

    private final CrudAccountsRepository crudAccountsRepository;

    @Autowired
    public SpringJdbcAccountsRepository(CrudAccountsRepository crudAccountsRepository) {
        this.crudAccountsRepository = crudAccountsRepository;
    }

    @Override
    public Account createAccount(Account account) {
        return crudAccountsRepository.save(account);
    }

    @Override
    public Account updateAccount(Account account) {
        return crudAccountsRepository.save(account);
    }

    @Override
    public Account getAccount(long id) {
        return crudAccountsRepository.getAccountById(id);
    }

    @Override
    public boolean deleteAccount(Account account) {
        account.setIsDeleted(1); // set deleted flag true
        Account updatedAccount = updateAccount(account);

        return updatedAccount != null && updatedAccount.getIsDeleted() == 1;
    }
}
