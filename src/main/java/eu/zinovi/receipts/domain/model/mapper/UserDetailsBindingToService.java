package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.user.UserDetailsBindingModel;
import eu.zinovi.receipts.domain.model.service.UserDetailsServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDetailsBindingToService {

        UserDetailsServiceModel map(UserDetailsBindingModel userDetailsBindingModel);
}
