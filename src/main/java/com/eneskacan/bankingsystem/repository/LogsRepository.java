package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.mapper.TransactionMapper;
import com.eneskacan.bankingsystem.util.FileReaderUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LogsRepository {

    @Value("${data.folder}")
    private String dataFolder;

    public List<String> getLogs() {
        List<String> csvLogs = FileReaderUtil.readFile(dataFolder + "\\logs.txt");
        List<String> logs = new ArrayList<>();

        for(String log : csvLogs) {
            String[] values = log.split(",");

            if(values[0].equals("deposit")) {
                logs.add(TransactionMapper.toDepositDto(log).toString());
            }

            if(values[0].equals("transfer")) {
                logs.add(TransactionMapper.toTransferDto(log).toString());
            }
        }

        return logs;
    }
}
