package com.eneskacan.bankingsystem.dto.request;

import lombok.Data;

@Data
public class DepositCreationRequest {
    private final String test;
    private final double amount;
}
