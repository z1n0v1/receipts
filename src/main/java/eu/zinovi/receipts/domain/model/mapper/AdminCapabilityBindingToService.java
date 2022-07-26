package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.admin.AdminCapabilityBindingModel;
import eu.zinovi.receipts.domain.model.service.AdminCapabilityServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminCapabilityBindingToService {
    AdminCapabilityServiceModel map(AdminCapabilityBindingModel adminCapabilityBindingModel);
}
