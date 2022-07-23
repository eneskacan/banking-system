package com.eneskacan.bankingsystem.service;

import com.eneskacan.bankingsystem.dto.generic.AccountDTO;
import com.eneskacan.bankingsystem.exception.DeletedAccountException;
import com.eneskacan.bankingsystem.exception.InvalidAccountTypeException;
import com.eneskacan.bankingsystem.exception.UnexpectedErrorException;
import com.eneskacan.bankingsystem.mapper.AccountMapper;
import com.eneskacan.bankingsystem.model.Account;
import com.eneskacan.bankingsystem.dto.request.AccountCreationRequest;
import com.eneskacan.bankingsystem.model.AssetTypes;
import com.eneskacan.bankingsystem.dto.response.AccountCreationResponse;
import com.eneskacan.bankingsystem.repository.IAccountsRepository;
import com.eneskacan.bankingsystem.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;

@Service
public class AccountsService {

    private final IAccountsRepository accountsRepository;

    @Autowired
    public AccountsService(@Qualifier("LocalAccountsRepository") IAccountsRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }

    public AccountCreationResponse createAccount(AccountCreationRequest request) throws InvalidAccountTypeException, UnexpectedErrorException {
        // Check if account type is valid
        boolean isTypeValid = false;
        for(AssetTypes t : AssetTypes.values()) {
            if(t.name().equalsIgnoreCase(request.getType())) {
                isTypeValid = true;
                break;
            }
        }

        // Check if account type is valid
        if(!isTypeValid) {
            String errorMessage = String.format("Invalid account type: " +
                    "Expected TRY, USD or XAU but got %s", request.getType());

            throw new InvalidAccountTypeException(errorMessage);
        }

        // Set last updated time as now
        Account account = AccountMapper.toAccount(request);
        final long updateTime = DateUtil.getTimestamp();
        account.setLastUpdated(updateTime);

        // Create account
        account = accountsRepository.createAccount(account);

        // Return here if fails to create the account
        if(account == null) {
            throw new UnexpectedErrorException("Failed to create account");
        }

        return AccountCreationResponse.builder()
                .message("Account successfully created")
                .id(account.getId())
                .build();
    }

    @Cacheable(cacheNames = {"accounts"}, key = "#id")
    public AccountDTO getAccount(long id) throws DeletedAccountException, AccountNotFoundException {
        // simulateBackendCall();

        Account account = accountsRepository.getAccount(id);

        if(account == null) {
            throw new AccountNotFoundException("Account is not found: " + id);
        }

        if(account.getIsDeleted() == 1) {
            throw new DeletedAccountException("Account is deleted: " + id);
        }

        return AccountMapper.toDto(account);
    }

    @CachePut(cacheNames = {"accounts"}, key="#dto.getId()")
    public AccountDTO updateAccount(AccountDTO dto) throws UnexpectedErrorException {
        // Update account details
        Account account = AccountMapper.toAccount(dto);

        // Save updated details
        if(accountsRepository.updateAccount(account) != null) {
            return dto;
        }

        throw new UnexpectedErrorException("Failed to update account details");
    }

    @CacheEvict(cacheNames = {"accounts"}, key="#id")
    public boolean deleteAccount(long id) throws DeletedAccountException, AccountNotFoundException {
        // Get account details
        AccountDTO accountDTO = getAccount(id);

        // Delete account
        return accountsRepository.deleteAccount(AccountMapper.toAccount(accountDTO));
    }

    // This method will pause main thread for 5 seconds
    private void simulateBackendCall() {
        try {
            System.out.println("------------- Going to sleep for 5 seconds to simulate Backend Delay -----------");
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
