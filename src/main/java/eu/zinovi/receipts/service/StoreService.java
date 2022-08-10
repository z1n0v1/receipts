package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.entity.Company;
import eu.zinovi.receipts.domain.model.entity.Store;

public interface StoreService {
    Store findByNameAndAddressAndCompany(String name, String address, Company company);
}
