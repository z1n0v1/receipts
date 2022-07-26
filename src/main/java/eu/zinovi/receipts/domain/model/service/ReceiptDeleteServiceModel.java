package eu.zinovi.receipts.domain.model.service;

import lombok.Data;

import java.util.UUID;

@Data
public class ReceiptDeleteServiceModel {
    private UUID receiptId;
}
