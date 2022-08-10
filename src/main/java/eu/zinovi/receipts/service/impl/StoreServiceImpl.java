package eu.zinovi.receipts.service.impl;

import eu.zinovi.receipts.domain.model.entity.Company;
import eu.zinovi.receipts.domain.model.entity.Store;
import eu.zinovi.receipts.repository.StoreRepository;
import eu.zinovi.receipts.service.AddressService;
import eu.zinovi.receipts.service.StoreService;
import org.springframework.stereotype.Service;

@Service
public class StoreServiceImpl implements StoreService {
    private final AddressService addressService;
    private final StoreRepository storeRepository;

    public StoreServiceImpl(AddressService addressService, StoreRepository storeRepository) {
        this.addressService = addressService;
        this.storeRepository = storeRepository;
    }

    @Override
    public Store findByNameAndAddressAndCompany(String name, String address, Company company) {

        Store store = storeRepository.findByNameAndAddressAndCompany(name, addressService.getAddress(address), company);

        if (store == null) {
            store = new Store();
            store.setName(name);
            store.setAddress(addressService.getAddress(address) );
            store.setCompany(company);
            storeRepository.save(store);
        }

        return store;
    }
}
