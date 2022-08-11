package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.entity.Address;

public interface AddressService {
    Address getAddress(String value);
}
