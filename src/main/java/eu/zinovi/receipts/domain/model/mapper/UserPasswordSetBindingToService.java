package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.user.UserPasswordSetBindingModel;
import eu.zinovi.receipts.domain.model.service.UserPasswordSetServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPasswordSetBindingToService {
    UserPasswordSetServiceModel map(UserPasswordSetBindingModel userPasswordSetBindingModel);
}
