package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.model.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Qualifier("SpringJdbcLogsRepository")
public class SpringJdbcLogsRepository implements ILogsRepository {

    private final CrudLogsRepository logsCrudRepository;

    @Autowired
    public SpringJdbcLogsRepository(CrudLogsRepository logsCrudRepository) {
        this.logsCrudRepository = logsCrudRepository;
    }

    @Override
    public boolean saveLog(Log l) {
        Log result = logsCrudRepository.save(l);
        return result != null;
    }

    @Override
    public List<Log> getLogs() {
        List<Log> logs = new ArrayList<>();
        logsCrudRepository.findAll().forEach(logs::add);
        return logs;
    }
}
