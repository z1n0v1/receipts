package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.entity.Address;
import eu.zinovi.receipts.domain.model.view.admin.AdminReceiptView;

import javax.transaction.Transactional;
import java.util.UUID;

public interface AddressService {
    Address getAddress(String value);

    @Transactional
    AdminReceiptView getReceipt(UUID receiptId);
}
