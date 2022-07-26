package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.entity.User;
import eu.zinovi.receipts.domain.model.binding.user.UserDetailsBindingModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserToBindingDetails {

        UserDetailsBindingModel map(User user);
}
