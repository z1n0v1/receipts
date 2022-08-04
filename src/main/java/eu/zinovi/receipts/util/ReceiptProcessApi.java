package eu.zinovi.receipts.util;

import java.io.InputStream;
import java.util.UUID;

public interface ReceiptProcessApi {
    void setReceiptId(UUID receiptId);

    String uploadReceipt(InputStream imageStream);

    void deleteReceipt(String receiptId);

    String doOCR();
}
