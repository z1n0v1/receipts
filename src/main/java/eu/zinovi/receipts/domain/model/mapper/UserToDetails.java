package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.entity.User;
import eu.zinovi.receipts.domain.model.view.UserDetailsView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserToDetails {

    @Mapping(source = "emailLoginDisabled", target = "emailLoginDisabled")
    UserDetailsView map(User user);
}
