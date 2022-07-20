package com.eneskacan.bankingsystem.controller;

import com.eneskacan.bankingsystem.dto.generic.AccountDTO;
import com.eneskacan.bankingsystem.dto.request.AccountCreationRequest;
import com.eneskacan.bankingsystem.dto.response.AccountCreationResponse;
import com.eneskacan.bankingsystem.dto.response.ErrorResponse;
import com.eneskacan.bankingsystem.exception.InvalidAccountTypeException;
import com.eneskacan.bankingsystem.service.AccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequestMapping("api/accounts")
public class AccountsController {

    private final AccountsService accountsService;

    @Autowired
    public AccountsController(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @PostMapping()
    public ResponseEntity<?> createAccount(@RequestBody AccountCreationRequest request) {
        try {
            AccountCreationResponse response = accountsService.createAccount(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        } catch (InvalidAccountTypeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getAccount(@PathVariable long id, WebRequest request) {
        AccountDTO account = accountsService.getAccount(id);

        // Check if account is modified
        if(request.checkNotModified(account.getLastUpdated())) {
            return ResponseEntity
                    .status(HttpStatus.NOT_MODIFIED)
                    .build();
        }

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.noCache())
                .lastModified(account.getLastUpdated())
                .body(account);
    }
}
