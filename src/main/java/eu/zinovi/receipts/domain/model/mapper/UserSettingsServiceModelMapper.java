package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.user.UserPasswordChangeBindingModel;
import eu.zinovi.receipts.domain.model.service.UserSettingsServiceModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserSettingsServiceModelMapper {

    UserSettingsServiceModelMapper INSTANCE = Mappers.getMapper(UserSettingsServiceModelMapper.class);

    @Mapping(source = "newPassword", target = "password")
    UserSettingsServiceModel userSettingsServiceModel(
            UserPasswordChangeBindingModel userPasswordChangeBindingModel);
}
