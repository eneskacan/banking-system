package com.eneskacan.bankingsystem.mapper;

import com.eneskacan.bankingsystem.dto.generic.DepositDTO;
import com.eneskacan.bankingsystem.dto.generic.TransferDTO;
import com.eneskacan.bankingsystem.model.AssetTypes;

public class TransactionMapper {

    private TransactionMapper() {
        throw new IllegalStateException("Utility class");
    }

    // Transforms coma separated values to Deposit DTO
    public static DepositDTO toDepositDto(String csv) {
        String[] values = csv.split(",");

        // Check if transaction type is correct
        if(!values[0].equals("deposit")) {
            return null;
        }

        return DepositDTO.builder()
                .accountNumber(values[1])
                .amount(Double.parseDouble(values[2]))
                .assetType(AssetTypes.valueOf(values[3]))
                .timestamp(Long.parseLong(values[4]))
                .build();
    }

    // Transforms coma separated values to Transfer DTO
    public static TransferDTO toTransferDto(String csv) {
        String[] values = csv.split(",");

        // Check if transaction type is correct
        if(!values[0].equals("transfer")) {
            return null;
        }

        return TransferDTO.builder()
                .sender(values[1])
                .receiver(values[2])
                .amount(Double.parseDouble(values[3]))
                .assetType(AssetTypes.valueOf(values[4]))
                .receivedAmount(Double.parseDouble(values[5]))
                .receivedAssetType(AssetTypes.valueOf(values[6]))
                .timestamp(Long.parseLong(values[7]))
                .build();
    }

    // Transforms Deposit DTO objects to coma separated values
    public static String toCsv(DepositDTO d) {
        return String.format("deposit,%s,%.3f,%s,%s",
                d.getAccountNumber(), d.getAmount(), d.getAssetType(), d.getTimestamp());
    }

    // Transforms Transfer DTO objects to coma separated values
    public static String toCsv(TransferDTO t) {
        return String.format("transfer,%s,%s,%.3f,%s,%.3f,%s,%s",
                t.getSender(), t.getReceiver(), t.getAmount(), t.getAssetType(),
                t.getReceivedAmount(), t.getReceivedAssetType(), t.getTimestamp());
    }
}
