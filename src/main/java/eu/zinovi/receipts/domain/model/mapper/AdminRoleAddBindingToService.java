package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.admin.AdminRoleAddBindingModel;
import eu.zinovi.receipts.domain.model.service.AdminRoleAddServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { AdminCapabilityAddBindingToService.class })
public interface AdminRoleAddBindingToService {
    AdminRoleAddServiceModel map(AdminRoleAddBindingModel adminRoleAddBindingModel);
}
