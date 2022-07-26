package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.entity.User;
import eu.zinovi.receipts.domain.model.view.admin.AdminUserView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface UserToAdminView {

    @Mapping(source = "picture", target = "picture")
    AdminUserView map(User user);

    default String formatDate(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

}
