package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.admin.AdminCategorySaveBindingModel;
import eu.zinovi.receipts.domain.model.service.AdminCategorySaveServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminCategorySaveBindingToService {
    AdminCategorySaveServiceModel map(AdminCategorySaveBindingModel model);
}
