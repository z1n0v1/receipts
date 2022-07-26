package eu.zinovi.receipts.service;

import eu.zinovi.receipts.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public boolean existsByName(String role) {
        return roleRepository.existsByName(role);
    }
}
