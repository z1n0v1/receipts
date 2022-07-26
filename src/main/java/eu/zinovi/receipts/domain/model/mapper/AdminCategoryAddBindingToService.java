package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.admin.AdminCategoryAddBindingModel;
import eu.zinovi.receipts.domain.model.service.AdminCategoryAddServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminCategoryAddBindingToService {
    AdminCategoryAddServiceModel map(AdminCategoryAddBindingModel adminCategoryAddBindingModel);
}
