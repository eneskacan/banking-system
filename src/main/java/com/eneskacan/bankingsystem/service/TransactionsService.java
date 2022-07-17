package com.eneskacan.bankingsystem.service;

import com.eneskacan.bankingsystem.dto.generic.AccountDTO;
import com.eneskacan.bankingsystem.dto.generic.DepositDTO;
import com.eneskacan.bankingsystem.dto.generic.TransferDTO;
import com.eneskacan.bankingsystem.mapper.AccountMapper;
import com.eneskacan.bankingsystem.model.Account;
import com.eneskacan.bankingsystem.model.AssetTypes;
import com.eneskacan.bankingsystem.util.DateUtil;
import com.eneskacan.bankingsystem.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransactionsService {

    private final AccountsService accountsService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private double USD_TRY;
    private double XAU_TRY;

    @Value("${collect.api.token}")
    private String accessToken;

    @Autowired
    public TransactionsService(AccountsService accountsService, KafkaTemplate<String, String> kafkaTemplate) {
        this.accountsService = accountsService;
        this.kafkaTemplate = kafkaTemplate;
    }

    public AccountDTO deposit(String accountNumber, double amount) {
        AccountDTO accountDTO = accountsService.getAccount(accountNumber);

        // Check if deposit accounts are valid
        if(accountDTO == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Account number is invalid"
            );
        }

        // Check if amount is valid
        if(amount <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Deposit amount is not valid: %s %s",
                            amount, accountDTO.getAccountType())
            );
        }

        // Update account balance
        Account account = AccountMapper.toAccount(accountDTO);
        account.deposit(amount);
        accountDTO = AccountMapper.toDto(account);

        // Update last update date
        long now = DateUtil.getTimestamp();
        account.setLastUpdated(now);

        // Save updated account
        accountsService.updateAccount(accountDTO);

        // Log
        DepositDTO deposit = DepositDTO.builder()
                .accountNumber(accountNumber)
                .amount(amount)
                .assetType(account.getAccountType())
                .timestamp(now)
                .build();
        kafkaTemplate.send("logs", deposit.toString());

        // Return updated account info
        return AccountMapper.toDto(account);
    }

    public AccountDTO transfer(String accountNumber, double amount, String receiverAccountNumber) {
        AccountDTO senderDTO = accountsService.getAccount(accountNumber);
        AccountDTO receiverDTO = accountsService.getAccount(receiverAccountNumber);

        // Check if sender and receiver accounts are valid
        if(senderDTO == null || receiverDTO == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Account number is invalid"
            );
        }

        // Check if amount is valid
        if(amount <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Transfer amount is not valid: %s %s",
                            amount, senderDTO.getAccountType())
            );
        }

        // Check if sender has sufficient funds
        if(senderDTO.getBalance() < amount) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    String.format("Sender account does not have enough funds: %s %s",
                            senderDTO.getBalance(), senderDTO.getAccountType())
            );
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
                .sender(accountNumber)
                .receiver(receiverAccountNumber)
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

    private void updateExchangeRates() {
        // Get US Dollar to Turkish Lira exchange rate
        try {
            String url = "https://api.collectapi.com/economy/singleCurrency?int=1&tag=USD";
            USD_TRY = HttpUtil.sendGetRequest(url, accessToken)
                    .getJSONArray("result")
                    .getJSONObject(0)
                    .getDouble("selling");
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Failed to get TRY/USD exchange data: %s", e)
            );
        }

        // Get gold price per gram in Turkish Lira
        try {
            String url = "https://api.collectapi.com/economy/goldPrice";
            XAU_TRY = HttpUtil.sendGetRequest(url, accessToken)
                    .getJSONArray("result")
                    .getJSONObject(0)
                    .getDouble("selling");
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Failed to get TRY/XAU exchange data: %s", e)
            );
        }
    }
}
