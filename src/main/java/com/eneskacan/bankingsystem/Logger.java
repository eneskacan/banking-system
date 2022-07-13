package com.eneskacan.bankingsystem;

import com.eneskacan.bankingsystem.util.FileWriterUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Logger {

    @Value("${data.folder}")
    private String dataFolder;

    @KafkaListener(topics = "logs", groupId = "foo")
    private void listenLogMessages(String message) {
        System.out.printf("Received message from Kafka: %s%n", message);
        FileWriterUtil.writeToFile(String.format("%s%n", message), dataFolder + "\\logs.txt", true);
    }
}
