package eu.zinovi.receipts.service.impl;

import eu.zinovi.receipts.repository.RoleRepository;
import eu.zinovi.receipts.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;


    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public boolean existsByName(String role) {
        return roleRepository.existsByName(role);
    }
}
