package eu.zinovi.receipts.repository;

import eu.zinovi.receipts.domain.model.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Company> findByEik(String eik);
}