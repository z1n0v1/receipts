package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.admin.AdminCategoryDeleteBindingModel;
import eu.zinovi.receipts.domain.model.service.AdminCategoryDeleteServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminCategoryDeleteBindingToService {
    AdminCategoryDeleteServiceModel map(AdminCategoryDeleteBindingModel model);
}
