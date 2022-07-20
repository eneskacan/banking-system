package com.eneskacan.bankingsystem.controller;

import com.eneskacan.bankingsystem.dto.generic.AccountDTO;
import com.eneskacan.bankingsystem.dto.request.AccountCreationRequest;
import com.eneskacan.bankingsystem.dto.response.AccountCreationResponse;
import com.eneskacan.bankingsystem.dto.response.ErrorResponse;
import com.eneskacan.bankingsystem.service.AccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

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
        } catch (ResponseStatusException e) {
            return ResponseEntity
                    .status(e.getStatus())
                    .body(new ErrorResponse(e.getReason()));
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
