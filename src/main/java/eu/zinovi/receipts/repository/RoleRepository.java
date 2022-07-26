package eu.zinovi.receipts.repository;

import eu.zinovi.receipts.domain.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> getByName(String roleName);

    boolean existsByName(String role);
}