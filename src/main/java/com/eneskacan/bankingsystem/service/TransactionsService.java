package com.eneskacan.bankingsystem.service;

import com.eneskacan.bankingsystem.dto.generic.AccountDTO;
import com.eneskacan.bankingsystem.dto.generic.DepositDTO;
import com.eneskacan.bankingsystem.dto.generic.TransferDTO;
import com.eneskacan.bankingsystem.exception.*;
import com.eneskacan.bankingsystem.mapper.AccountMapper;
import com.eneskacan.bankingsystem.model.Account;
import com.eneskacan.bankingsystem.model.AssetTypes;
import com.eneskacan.bankingsystem.repository.IExchangeRepository;
import com.eneskacan.bankingsystem.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;

@Service
public class TransactionsService {

    private final AccountsService accountsService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final IExchangeRepository exchangeRepository;

    private double USD_TRY;
    private double XAU_TRY;

    @Autowired
    public TransactionsService(AccountsService accountsService,
                               KafkaTemplate<String, String> kafkaTemplate,
                               @Qualifier("CollectApiExchangeRepository") IExchangeRepository exchangeRepository) {
        this.accountsService = accountsService;
        this.kafkaTemplate = kafkaTemplate;
        this.exchangeRepository = exchangeRepository;
    }

    public AccountDTO deposit(long id, double amount)
            throws AccountNotFoundException, DeletedAccountException, InvalidInputException, UnexpectedErrorException {
        AccountDTO accountDTO = accountsService.getAccount(id);

        // Check if amount is valid
        if(amount <= 0) {
            String errorMessage = String.format("Deposit amount is not valid: %s %s",
                    amount, accountDTO.getAccountType());

            throw new InvalidInputException(errorMessage);
        }

        // Update account balance
        Account account = AccountMapper.toAccount(accountDTO);
        account.deposit(amount);

        // Update last update date
        long now = DateUtil.getTimestamp();
        account.setLastUpdated(now);

        // Save updated account
        accountDTO = AccountMapper.toDto(account);
        accountsService.updateAccount(accountDTO);

        // Log
        DepositDTO deposit = DepositDTO.builder()
                .id(id)
                .amount(amount)
                .assetType(account.getAccountType())
                .timestamp(now)
                .build();
        kafkaTemplate.send("logs", deposit.toString());

        // Return updated account info
        return AccountMapper.toDto(account);
    }

    public AccountDTO transfer(long senderId, double amount, long receiverId)
            throws AccountNotFoundException, DeletedAccountException, InvalidInputException,
            InsufficientFundsException, FailingApiCallException, UnexpectedErrorException {
        AccountDTO senderDTO = accountsService.getAccount(senderId);
        AccountDTO receiverDTO = accountsService.getAccount(receiverId);

        // Check if amount is valid
        if(amount <= 0) {
            String errorMessage = String.format("Transfer amount is not valid: %s %s",
                    amount, senderDTO.getAccountType());

            throw new InvalidInputException(errorMessage);
        }

        // Check if sender has sufficient funds
        if(senderDTO.getBalance() < amount) {
            String errorMessage = String.format("Sender account does not have enough funds: %s %s",
                    senderDTO.getBalance(), senderDTO.getAccountType());

            throw new InsufficientFundsException(errorMessage);
        }

        // Convert amount if accounts have different asset types
        double exchangeRate = 1;
        if(!senderDTO.getAccountType().equals(receiverDTO.getAccountType())) {
            AssetTypes base = senderDTO.getAccountType();
            AssetTypes to = receiverDTO.getAccountType();

            // Get latest exchange rates
            updateExchangeRates();

            // Convert USD and XAU to TRY if needed
            if(base.equals(AssetTypes.USD)) exchangeRate *= USD_TRY;
            if(base.equals(AssetTypes.XAU)) exchangeRate *= XAU_TRY;

            // Convert TRY to USD or XAU if needed
            if(to.equals(AssetTypes.USD)) exchangeRate /= USD_TRY;
            if(to.equals(AssetTypes.XAU)) exchangeRate /= XAU_TRY;
        }

        // Get current time
        long now = DateUtil.getTimestamp();

        // Reduce sender account balance
        Account sender = AccountMapper.toAccount(senderDTO);
        sender.withdraw(amount);
        sender.setLastUpdated(now);
        senderDTO = AccountMapper.toDto(sender);

        // Increase receiver account balance
        Account receiver = AccountMapper.toAccount(receiverDTO);
        receiver.deposit(amount * exchangeRate);
        receiver.setLastUpdated(now);
        receiverDTO = AccountMapper.toDto(receiver);

        // Save updated accounts
        accountsService.updateAccount(senderDTO);
        accountsService.updateAccount(receiverDTO);

        // Log
        TransferDTO transfer = TransferDTO.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .amount(amount)
                .assetType(sender.getAccountType())
                .receivedAmount(amount * exchangeRate)
                .receivedAssetType(receiver.getAccountType())
                .timestamp(now)
                .build();
        kafkaTemplate.send("logs", transfer.toString());

        // Return updated account info
        return AccountMapper.toDto(sender);
    }

    private void updateExchangeRates() throws FailingApiCallException {
        try {
            USD_TRY = exchangeRepository.getUsdTryExchangeRate();
        } catch (Exception e) {
            String errorMessage = String.format("Failed to get TRY/USD exchange data: %s", e);

            throw new FailingApiCallException(errorMessage);
        }

        try {
            XAU_TRY = exchangeRepository.getXauTryExchangeRate();
        } catch (Exception e) {
            String errorMessage = String.format("Failed to get TRY/XAU exchange data: %s", e);

            throw new FailingApiCallException(errorMessage);
        }
    }
}
