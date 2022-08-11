package eu.zinovi.receipts.service.impl;

import eu.zinovi.receipts.domain.exception.AccessDeniedException;
import eu.zinovi.receipts.domain.exception.EntityNotFoundException;
import eu.zinovi.receipts.domain.model.entity.Capability;
import eu.zinovi.receipts.domain.model.entity.Role;
import eu.zinovi.receipts.domain.model.enums.CapabilityEnum;
import eu.zinovi.receipts.domain.model.mapper.AdminRoleToView;
import eu.zinovi.receipts.domain.model.service.*;
import eu.zinovi.receipts.domain.model.view.admin.AdminRoleView;
import eu.zinovi.receipts.repository.CapabilityRepository;
import eu.zinovi.receipts.repository.RoleRepository;
import eu.zinovi.receipts.repository.UserRepository;
import eu.zinovi.receipts.service.RoleService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static eu.zinovi.receipts.util.constants.MessageConstants.NO_PERMISSION_ROLE_DELETE_WITH_ACTIVE_USERS;

@Service
public class RoleServiceImpl implements RoleService {
    private final AdminRoleToView adminRoleToView;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CapabilityRepository capabilityRepository;


    public RoleServiceImpl(
            AdminRoleToView adminRoleToView,
            UserRepository userRepository,
            RoleRepository roleRepository,
            CapabilityRepository capabilityRepository) {
        this.adminRoleToView = adminRoleToView;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.capabilityRepository = capabilityRepository;
    }

    @Override
    public void addRole(AdminRoleAddServiceModel AdminRoleAddServiceModel) {
        Role role = new Role();
        role.setName(AdminRoleAddServiceModel.getName());

        for (AdminCapabilityAddServiceModel capability : AdminRoleAddServiceModel.getCapabilities()) {
            CapabilityEnum capabilityEnum = capability.getName();
            Capability cap = capabilityRepository.getByName(capabilityEnum).orElseThrow(EntityNotFoundException::new);
            role.getCapabilities().add(cap);
        }
        roleRepository.save(role);
    }

    @Override
    public boolean existsByName(String role) {
        return roleRepository.existsByName(role);
    }

    @Override
    @Transactional
    public List<AdminRoleView> listRoles() {
        return roleRepository.findAll().stream()
                .map(adminRoleToView::map)
                .toList();

    }

    @Override
    public void updateRole(AdminRoleServiceModel adminRoleServiceModel) {

        if (adminRoleServiceModel.getRole().equals("ADMIN")) {
            throw new AccessDeniedException("Нямате право да редактирате ролята ADMIN");
        }

        Role role = roleRepository.getByName(adminRoleServiceModel.getRole()).orElseThrow(EntityNotFoundException::new);

        Collection<Capability> capabilities = new ArrayList<>();

        for (AdminCapabilityServiceModel adminCapabilityServiceModel : adminRoleServiceModel.getCapabilities()) {
            if (adminCapabilityServiceModel.getActive()) {
//                CapabilityEnum capabilityEnum = CapabilityEnum.valueOf(capabilityView.getCapability());
                Capability capability = capabilityRepository.getByName(adminCapabilityServiceModel.getCapability())
                        .orElseThrow(EntityNotFoundException::new);
                capabilities.add(capability);
            }
        }
        role.setCapabilities(capabilities);
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void deleteRole(AdminRoleDeleteServiceModel adminRoleDeleteServiceModel) {

        if (adminRoleDeleteServiceModel.getName().equals("ADMIN") ||
                adminRoleDeleteServiceModel.getName().equals("USER")) {
            throw new AccessDeniedException("Не можете да изтриете ролите ADMIN и USER");
        }

        userRepository.findAll().forEach(user -> {
            if (user.getRoles().stream().anyMatch(
                    role -> role.getName().equals(adminRoleDeleteServiceModel.getName()))) {
                throw new AccessDeniedException(NO_PERMISSION_ROLE_DELETE_WITH_ACTIVE_USERS);
            }});

        Role role = roleRepository.getByName(adminRoleDeleteServiceModel.getName())
                .orElseThrow(EntityNotFoundException::new);
        roleRepository.delete(role);
    }
}
