package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.admin.AdminUserDeleteBindingModel;
import eu.zinovi.receipts.domain.model.service.AdminUserDeleteServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminUserDeleteBindingToService {
    AdminUserDeleteServiceModel map(AdminUserDeleteBindingModel adminUserDeleteBindingModel);
}
