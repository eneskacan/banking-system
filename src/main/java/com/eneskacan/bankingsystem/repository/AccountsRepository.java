package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.mapper.AccountMapper;
import com.eneskacan.bankingsystem.model.Account;
import com.eneskacan.bankingsystem.util.FileReaderUtil;
import com.eneskacan.bankingsystem.util.FileWriterUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepository {
    @Value("${data.folder}")
    private String folderPath;

    public boolean saveOrUpdateAccount(Account a) {
        final String filePath = String.format("%s%s.txt", folderPath, a.getAccountNumber());
        return FileWriterUtil.writeToFile(AccountMapper.toCsv(a), filePath, false);
    }

    public Account getAccount(String accountNumber) {
        final String filePath = String.format("%s%s.txt", folderPath, accountNumber);
        return AccountMapper.toAccount(FileReaderUtil.readFile(filePath).get(0));
    }
}
