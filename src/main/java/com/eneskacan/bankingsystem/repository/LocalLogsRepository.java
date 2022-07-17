package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.model.Log;
import com.eneskacan.bankingsystem.util.FileReaderUtil;
import com.eneskacan.bankingsystem.util.FileWriterUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Qualifier("LocalAccountsRepository")
public class LocalLogsRepository implements ILogsRepository {

    @Value("${data.folder}")
    private String dataFolder;

    @Override
    public boolean saveLog(Log l) {
        FileWriterUtil.writeToFile(
                String.format("%s%n", l.getMessage()),
                dataFolder + "logs.txt",
                true);
        return true;
    }

    @Override
    public List<Log> getLogs() {
        return FileReaderUtil.readFile(dataFolder + "logs.txt").stream()
                .map(Log::new)
                .collect(Collectors.toList());
    }
}
