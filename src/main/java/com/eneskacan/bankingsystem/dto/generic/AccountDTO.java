package com.eneskacan.bankingsystem.dto.generic;

import com.eneskacan.bankingsystem.model.AssetTypes;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class AccountDTO {
    private final long id;
    private final String name;
    private final String surname;
    private final String email;
    private final String idNumber;
    private final AssetTypes accountType;
    private final double balance;
    private final Timestamp lastUpdated;
}
