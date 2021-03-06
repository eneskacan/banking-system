package com.eneskacan.bankingsystem.controller;

import com.eneskacan.bankingsystem.dto.response.ErrorResponse;
import com.eneskacan.bankingsystem.exception.UnexpectedErrorException;
import com.eneskacan.bankingsystem.model.Log;
import com.eneskacan.bankingsystem.service.LogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/logs")
public class LogsController {

    private final LogsService logsService;

    @Autowired
    public LogsController(LogsService logsService) {
        this.logsService = logsService;
    }

    @CrossOrigin("http://localhost:6062")
    @GetMapping
    public ResponseEntity<?> getLogs() {
        try {
            List<Log> logs = logsService.getLogs();
            return ResponseEntity
                    .ok()
                    .body(logs);
        } catch (UnexpectedErrorException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}
