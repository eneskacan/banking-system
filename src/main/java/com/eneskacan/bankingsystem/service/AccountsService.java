package com.eneskacan.bankingsystem.service;

import com.eneskacan.bankingsystem.dto.generic.AccountDTO;
import com.eneskacan.bankingsystem.mapper.AccountMapper;
import com.eneskacan.bankingsystem.model.Account;
import com.eneskacan.bankingsystem.dto.request.AccountCreationRequest;
import com.eneskacan.bankingsystem.model.AssetTypes;
import com.eneskacan.bankingsystem.repository.AccountsRepository;
import com.eneskacan.bankingsystem.dto.response.AccountCreationResponse;
import com.eneskacan.bankingsystem.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountsService {
    private final AccountsRepository accountsRepository;

    @Autowired
    public AccountsService(AccountsRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }

    public AccountCreationResponse createAccount(AccountCreationRequest request) {
        // Check if account type is valid
        boolean isTypeValid = false;
        for(AssetTypes t : AssetTypes.values()) {
            if(t.name().equalsIgnoreCase(request.getType())) {
                isTypeValid = true;
                break;
            }
        }

        // Throw exception if type is not valid
        if(!isTypeValid) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Invalid account type: " +
                        "Expected TRY, USD or UAX but got %s", request.getType())
            );
        }

        // Generate account number and last updated date
        final String accountNumber = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L + "";
        final long updateTime = DateUtil.getTimestamp();

        // Set account number and update time
        Account account = AccountMapper.toAccount(request);
        account.setAccountNumber(accountNumber);
        account.setLastUpdated(updateTime);

        // Create account
        if(accountsRepository.saveOrUpdateAccount(account)) {
            return AccountCreationResponse.builder()
                    .message("Account successfully created")
                    .accountNumber(accountNumber)
                    .build();
        }

        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create account");
    }

    public AccountDTO getAccount(String accountNumber) {
        Account account = accountsRepository.getAccount(accountNumber);
        return AccountMapper.toDto(account);
    }
}
