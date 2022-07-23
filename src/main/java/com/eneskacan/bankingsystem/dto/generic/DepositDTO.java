package com.eneskacan.bankingsystem.dto.generic;

import com.eneskacan.bankingsystem.model.AssetTypes;
import com.eneskacan.bankingsystem.util.DateUtil;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class DepositDTO {
    private final long id;
    private final double amount;
    private final AssetTypes assetType;
    private final Timestamp timestamp;

    @Override
    public String toString() {
        return String.format("Account %s deposited %.3f %s on %s",
                id, amount, assetType, timestamp);
    }
}
