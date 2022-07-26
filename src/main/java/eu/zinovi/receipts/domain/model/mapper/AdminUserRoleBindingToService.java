package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.admin.AdminUserRoleBindingModel;
import eu.zinovi.receipts.domain.model.service.AdminUserRoleServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminUserRoleBindingToService {
    AdminUserRoleServiceModel map(AdminUserRoleBindingModel adminUserRoleBindingModel);
}
