package eu.zinovi.receipts.repository;

import eu.zinovi.receipts.domain.model.entity.Capability;
import eu.zinovi.receipts.domain.model.enums.CapabilityEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CapabilityRepository extends JpaRepository<Capability, UUID> {
    Optional<Capability> getByName(CapabilityEnum name);
}