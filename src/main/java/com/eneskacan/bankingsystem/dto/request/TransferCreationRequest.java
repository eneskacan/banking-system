package com.eneskacan.bankingsystem.dto.request;

import lombok.Data;

@Data
public class TransferCreationRequest {
    private final long receiverId;
    private final double amount;
}
