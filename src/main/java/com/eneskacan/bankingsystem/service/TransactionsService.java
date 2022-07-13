package com.eneskacan.bankingsystem.service;

import com.eneskacan.bankingsystem.dto.generic.AccountDTO;
import com.eneskacan.bankingsystem.dto.generic.DepositDTO;
import com.eneskacan.bankingsystem.dto.generic.TransferDTO;
import com.eneskacan.bankingsystem.mapper.AccountMapper;
import com.eneskacan.bankingsystem.mapper.TransactionMapper;
import com.eneskacan.bankingsystem.model.Account;
import com.eneskacan.bankingsystem.model.AssetTypes;
import com.eneskacan.bankingsystem.repository.AccountsRepository;
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

    private final AccountsRepository accountsRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private double USD_TRY;
    private double XAU_TRY;

    @Value("${collect.api.token}")
    private String accessToken;

    @Autowired
    public TransactionsService(AccountsRepository accountsRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.accountsRepository = accountsRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public AccountDTO deposit(String accountNumber, double amount) {
        Account account = accountsRepository.getAccount(accountNumber);

        // Check if deposit accounts are valid
        if(account == null) {
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
                            amount, account.getAccountType())
            );
        }

        // Update account balance
        account.deposit(amount);

        // Update last update date
        long now = DateUtil.getTimestamp();
        account.setLastUpdated(now);

        // Save updated account
        accountsRepository.saveOrUpdateAccount(account);

        // Log
        DepositDTO deposit = DepositDTO.builder()
                .accountNumber(accountNumber)
                .amount(amount)
                .assetType(account.getAccountType())
                .timestamp(now)
                .build();
        kafkaTemplate.send("logs", TransactionMapper.toCsv(deposit));

        // Return updated account info
        return AccountMapper.toDto(account);
    }

    public AccountDTO transfer(String accountNumber, double amount, String receiverAccountNumber) {
        Account sender = accountsRepository.getAccount(accountNumber);
        Account receiver = accountsRepository.getAccount(receiverAccountNumber);

        // Check if sender and receiver accounts are valid
        if(sender == null || receiver == null) {
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
                            amount, sender.getAccountType())
            );
        }

        // Check if sender has sufficient funds
        if(sender.getBalance() < amount) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    String.format("Sender account does not have enough funds: %s %s",
                            sender.getBalance(), sender.getAccountType())
            );
        }

        // Convert amount if accounts have different asset types
        double exchangeRate = 1;
        if(!sender.getAccountType().equals(receiver.getAccountType())) {
            AssetTypes base = sender.getAccountType();
            AssetTypes to = receiver.getAccountType();

            // Get latest exchange rates
            updateExchangeRates();

            // Convert USD and XAU to TRY if needed
            if(base.equals(AssetTypes.USD)) exchangeRate *= USD_TRY;
            if(base.equals(AssetTypes.XAU)) exchangeRate *= XAU_TRY;

            // Convert TRY to USD or XAU if needed
            if(to.equals(AssetTypes.USD)) exchangeRate /= USD_TRY;
            if(to.equals(AssetTypes.XAU)) exchangeRate /= XAU_TRY;
        }

        // Update account balances
        sender.withdraw(amount);
        receiver.deposit(amount * exchangeRate);

        // Update last update dates
        long now = DateUtil.getTimestamp();
        sender.setLastUpdated(now);
        receiver.setLastUpdated(now);

        // Save updated accounts
        accountsRepository.saveOrUpdateAccount(sender);
        accountsRepository.saveOrUpdateAccount(receiver);

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
        kafkaTemplate.send("logs", TransactionMapper.toCsv(transfer));

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
