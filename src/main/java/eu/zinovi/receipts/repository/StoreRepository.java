package eu.zinovi.receipts.repository;

import eu.zinovi.receipts.domain.model.entity.Address;
import eu.zinovi.receipts.domain.model.entity.Company;
import eu.zinovi.receipts.domain.model.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
    Store findByNameAndAddressAndCompany(String name, Address address, Company company);
}