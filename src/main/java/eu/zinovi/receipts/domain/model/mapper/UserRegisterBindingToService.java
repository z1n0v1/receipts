package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.user.UserRegisterBindingModel;
import eu.zinovi.receipts.domain.model.service.UserRegisterServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRegisterBindingToService {
    UserRegisterServiceModel map(UserRegisterBindingModel userRegisterBindingModel);
}
