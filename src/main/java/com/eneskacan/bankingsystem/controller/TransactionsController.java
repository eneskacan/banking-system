package com.eneskacan.bankingsystem.controller;

import com.eneskacan.bankingsystem.dto.generic.AccountDTO;
import com.eneskacan.bankingsystem.dto.request.DepositCreationRequest;
import com.eneskacan.bankingsystem.dto.request.TransferCreationRequest;
import com.eneskacan.bankingsystem.dto.response.ErrorResponse;
import com.eneskacan.bankingsystem.exception.*;
import com.eneskacan.bankingsystem.service.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@RequestMapping("api/accounts/{id}")
public class TransactionsController {

    private final TransactionsService transactionsService;

    @Autowired
    public TransactionsController(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @PatchMapping("/deposits")
    public ResponseEntity<?> deposit(@RequestBody DepositCreationRequest request, @PathVariable long id) {
        try {
            AccountDTO account = transactionsService.deposit(id, request.getAmount());
            return ResponseEntity
                    .ok()
                    .lastModified(account.getLastUpdated())
                    .body(account);
        } catch (InvalidInputException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (AccountNotFoundException | DeletedAccountException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (UnexpectedErrorException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PatchMapping("/transfers")
    public ResponseEntity<?> transfer(@RequestBody TransferCreationRequest request, @PathVariable long id) {
        try {
            AccountDTO account = transactionsService.transfer(
                    id,
                    request.getAmount(),
                    request.getReceiverId());
            return ResponseEntity
                    .ok()
                    .lastModified(account.getLastUpdated())
                    .body(account);
        } catch (InvalidInputException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (InsufficientFundsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (AccountNotFoundException | DeletedAccountException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (FailingApiCallException | UnexpectedErrorException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}
