package eu.zinovi.receipts.repository;

import eu.zinovi.receipts.domain.model.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    Optional<Address> findByValue(String value);
}