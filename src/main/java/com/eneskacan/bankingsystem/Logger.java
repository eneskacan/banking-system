package com.eneskacan.bankingsystem;

import com.eneskacan.bankingsystem.exception.UnexpectedErrorException;
import com.eneskacan.bankingsystem.model.Log;
import com.eneskacan.bankingsystem.service.LogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Logger {

    private final LogsService logsService;

    @Autowired
    public Logger(LogsService logsService) {
        this.logsService = logsService;
    }

    @KafkaListener(topics = "logs", groupId = "foo")
    private void listenLogMessages(String message) throws UnexpectedErrorException {
        System.out.printf("Received message from Kafka: %s%n", message);
        logsService.saveLog(new Log(message));
    }
}
