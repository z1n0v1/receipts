package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.entity.ReceiptImage;

import javax.transaction.Transactional;
import java.util.UUID;

public interface ReceiptProcessService {
    @Transactional
    UUID parseReceipt(String processedMLJson, ReceiptImage receiptImage,
                      String fileName, String qrCode);
}
