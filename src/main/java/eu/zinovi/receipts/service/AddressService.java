package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.exception.EntityNotFoundException;
import eu.zinovi.receipts.domain.model.entity.Address;
import eu.zinovi.receipts.domain.model.entity.Receipt;
import eu.zinovi.receipts.domain.model.mapper.ReceiptToAdminView;
import eu.zinovi.receipts.domain.model.view.admin.AdminReceiptView;
import eu.zinovi.receipts.repository.AddressRepository;
import eu.zinovi.receipts.repository.ReceiptRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AddressService {
    private final ReceiptToAdminView receiptToAdminView;

    private final AddressRepository addressRepository;
    private final ReceiptRepository receiptRepository;

    public AddressService(ReceiptToAdminView receiptToAdminView, AddressRepository addressRepository, ReceiptRepository receiptRepository) {
        this.receiptToAdminView = receiptToAdminView;
        this.addressRepository = addressRepository;
        this.receiptRepository = receiptRepository;
    }

    public Address getAddress(String value) {
        Address address = addressRepository.findByValue(value).orElse(null);

        if (address == null) {
            address = new Address();
            address.setValue(value);

            Matcher matcher = Pattern.compile("бул|ул").matcher(value.toLowerCase());
            if (matcher.find()) {
                address.setCity(value.substring(0, matcher.start())
                        .replace(" ", "")
                        .replace(",", ""));
                address.setStreet(value.substring(matcher.start()));
            }
            addressRepository.save(address);
        }
        return address;
    }

    @Transactional
    public AdminReceiptView getReceipt(UUID receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId).orElseThrow(EntityNotFoundException::new);

        return receiptToAdminView.map(receipt);
    }
}
