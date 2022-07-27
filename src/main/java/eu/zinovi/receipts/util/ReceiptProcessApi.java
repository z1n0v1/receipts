package eu.zinovi.receipts.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public interface ReceiptProcessApi {
    void setReceiptId(UUID receiptId);

    String uploadReceipt(InputStream imageStream);

    String doOCR();
}
