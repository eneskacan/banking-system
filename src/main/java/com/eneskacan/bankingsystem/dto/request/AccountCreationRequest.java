package com.eneskacan.bankingsystem.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountCreationRequest {
    private final String name;
    private final String surname;
    private final String email;
    private final String idNumber;
    private final String type;
}
