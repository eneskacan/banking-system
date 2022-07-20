package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.mapper.AccountMapper;
import com.eneskacan.bankingsystem.model.Account;
import com.eneskacan.bankingsystem.util.FileReaderUtil;
import com.eneskacan.bankingsystem.util.FileWriterUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("LocalAccountsRepository")
public class LocalAccountsRepository implements IAccountsRepository {
    @Value("${data.folder}")
    private String folderPath;

    @Override
    public Account saveAccount(Account account) {
        // Set an id for account
        final long id = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
        account.setId(id);

        // Save account details to a text file in CSV format
        final String filePath = String.format("%s%s.txt", folderPath, account.getId());
        FileWriterUtil.writeToFile(AccountMapper.toCsv(account), filePath, false);

        // Return saved account
        return account;
    }

    @Override
    public Account getAccount(long id) {
        final String filePath = String.format("%s%s.txt", folderPath, id);
        return AccountMapper.toAccount(FileReaderUtil.readFile(filePath).get(0));
    }
}
