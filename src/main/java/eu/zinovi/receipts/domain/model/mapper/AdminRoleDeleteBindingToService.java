package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.admin.AdminRoleDeleteBindingModel;
import eu.zinovi.receipts.domain.model.service.AdminRoleDeleteServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminRoleDeleteBindingToService {

        AdminRoleDeleteServiceModel map(AdminRoleDeleteBindingModel adminRoleDeleteBindingModel);

}
