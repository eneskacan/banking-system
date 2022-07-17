package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.model.Log;

import java.util.List;

public interface ILogsRepository {
    boolean saveLog(Log l);
    List<Log> getLogs();
}
