package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.mybatis.LogMapper;
import com.eneskacan.bankingsystem.model.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("MyBatisLogsRepository")
public class MyBatisLogsRepository implements ILogsRepository {

    private final LogMapper logMapper;

    @Autowired
    public MyBatisLogsRepository(LogMapper logMapper) {
        this.logMapper = logMapper;
    }

    @Override
    public boolean saveLog(Log l) {
        return logMapper.save(l.getMessage());
    }

    @Override
    public List<Log> getLogs() {
        return logMapper.getAll();
    }
}
