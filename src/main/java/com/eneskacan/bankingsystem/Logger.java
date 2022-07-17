package com.eneskacan.bankingsystem;

import com.eneskacan.bankingsystem.mapper.TransactionMapper;
import com.eneskacan.bankingsystem.model.Log;
import com.eneskacan.bankingsystem.repository.ILogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class Logger {

    private final ILogsRepository logsRepository;

    @Autowired
    public Logger(@Qualifier("LocalAccountsRepository") ILogsRepository logsRepository) {
        this.logsRepository = logsRepository;
    }

    @KafkaListener(topics = "logs", groupId = "foo")
    private void listenLogMessages(String message) {
        System.out.printf("Received message from Kafka: %s%n", message);

        Log log = null;
        String[] values = message.split(",");

        if(values[0].equals("deposit")) {
            log = new Log(Objects.requireNonNull(TransactionMapper.toDepositDto(message)).toString());
        }

        if(values[0].equals("transfer")) {
            log = new Log(Objects.requireNonNull(TransactionMapper.toTransferDto(message)).toString());
        }

        if(log != null) {
            logsRepository.saveLog(log);
        }
    }
}
