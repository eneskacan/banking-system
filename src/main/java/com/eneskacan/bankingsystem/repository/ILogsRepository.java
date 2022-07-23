package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.exception.UnexpectedErrorException;
import com.eneskacan.bankingsystem.model.Log;

import java.util.List;

public interface ILogsRepository {
    boolean saveLog(Log l) throws UnexpectedErrorException;
    List<Log> getLogs() throws UnexpectedErrorException;
}
