package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.admin.AdminUserSaveBindingModel;
import eu.zinovi.receipts.domain.model.service.AdminUserSaveServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { AdminUserRoleBindingToService.class })
public interface AdminUserSaveBindingToService {
        AdminUserSaveServiceModel map(AdminUserSaveBindingModel adminUserSaveBindingModel);
}
