package com.eneskacan.bankingsystem.dto.generic;

import com.eneskacan.bankingsystem.model.AssetTypes;
import com.eneskacan.bankingsystem.util.DateUtil;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferDTO {
    private final long senderId;
    private final long receiverId;
    private final double amount;
    private final AssetTypes assetType;
    private final double receivedAmount;
    private final AssetTypes receivedAssetType;
    private final long timestamp;

    @Override
    public String toString() {
        return String.format("Account %s transferred %.3f %s (%.3f %s) to account %s on %s",
                senderId, amount, assetType, receivedAmount, receivedAssetType,
                receiverId, DateUtil.timestampToDate(timestamp));
    }
}
