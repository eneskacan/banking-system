package com.eneskacan.bankingsystem.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountCreationResponse {
    private final String message;
    private final long id;
}
