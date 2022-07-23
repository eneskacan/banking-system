package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrudAccountsRepository extends CrudRepository<Account, Long> {
    Account getAccountById(Long id);
}
