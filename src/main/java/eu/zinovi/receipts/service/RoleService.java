package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.service.AdminRoleAddServiceModel;
import eu.zinovi.receipts.domain.model.service.AdminRoleDeleteServiceModel;
import eu.zinovi.receipts.domain.model.service.AdminRoleServiceModel;
import eu.zinovi.receipts.domain.model.view.admin.AdminRoleView;

import javax.transaction.Transactional;
import java.util.List;

public interface RoleService {
    void addRole(AdminRoleAddServiceModel AdminRoleAddServiceModel);

    boolean existsByName(String role);

    @Transactional
    List<AdminRoleView> listRoles();

    void updateRole(AdminRoleServiceModel adminRoleServiceModel);

    @Transactional
    void deleteRole(AdminRoleDeleteServiceModel adminRoleDeleteServiceModel);
}
