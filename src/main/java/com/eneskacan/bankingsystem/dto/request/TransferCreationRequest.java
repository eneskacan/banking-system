package com.eneskacan.bankingsystem.dto.request;

import lombok.Data;

@Data
public class TransferCreationRequest {
    private final String receiverAccountNumber;
    private final double amount;
}
