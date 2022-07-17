package com.eneskacan.bankingsystem.service;

import com.eneskacan.bankingsystem.model.Log;
import com.eneskacan.bankingsystem.repository.ILogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogsService {

    private final ILogsRepository logsRepository;

    @Autowired
    public LogsService(@Qualifier("LocalAccountsRepository") ILogsRepository logsRepository) {
        this.logsRepository = logsRepository;
    }

    public List<Log> getLogs() {
        return logsRepository.getLogs();
    }
}
