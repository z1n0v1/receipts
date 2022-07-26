package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.admin.AdminRoleBindingModel;
import eu.zinovi.receipts.domain.model.service.AdminRoleServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { AdminCapabilityBindingToService.class })
public interface AdminRoleBindingToService {
    AdminRoleServiceModel map(AdminRoleBindingModel adminRoleBindingModel);
}
