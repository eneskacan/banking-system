package com.eneskacan.bankingsystem.controller;

import com.eneskacan.bankingsystem.dto.generic.AccountDTO;
import com.eneskacan.bankingsystem.dto.request.DepositCreationRequest;
import com.eneskacan.bankingsystem.dto.request.TransferCreationRequest;
import com.eneskacan.bankingsystem.dto.response.ErrorResponse;
import com.eneskacan.bankingsystem.service.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/accounts/{accountNumber}")
public class TransactionsController {

    private final TransactionsService transactionsService;

    @Autowired
    public TransactionsController(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @PatchMapping("/deposits")
    public ResponseEntity<?> deposit(@RequestBody DepositCreationRequest request,
                                              @PathVariable String accountNumber) {
        try {
            AccountDTO account = transactionsService.deposit(accountNumber, request.getAmount());
            return ResponseEntity
                    .ok()
                    .lastModified(account.getLastUpdated())
                    .body(account);
        } catch (ResponseStatusException e) {
            return ResponseEntity
                    .status(e.getStatus())
                    .body(new ErrorResponse(e.getReason()));
        }
    }

    @PatchMapping("/transfers")
    public ResponseEntity<?> transfer(@RequestBody TransferCreationRequest request,
                                               @PathVariable String accountNumber) {
        try {
            AccountDTO account = transactionsService.transfer(
                    accountNumber,
                    request.getAmount(),
                    request.getReceiverAccountNumber());
            return ResponseEntity
                    .ok()
                    .lastModified(account.getLastUpdated())
                    .body(account);
        } catch (ResponseStatusException e) {
            return ResponseEntity
                    .status(e.getStatus())
                    .body(new ErrorResponse(e.getReason()));
        }
    }
}
