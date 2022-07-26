package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.admin.AdminCapabilityAddBindingModel;
import eu.zinovi.receipts.domain.model.service.AdminCapabilityAddServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminCapabilityAddBindingToService {
    AdminCapabilityAddServiceModel map(AdminCapabilityAddBindingModel adminCapabilityAddBindingModel);
}
