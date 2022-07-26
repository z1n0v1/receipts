package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.entity.Capability;
import eu.zinovi.receipts.domain.model.view.admin.AdminCapabilityView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CapabilityToAdminView {

    @Mappings({
            @Mapping(source = "capability.name", target = "capability"),
    })
    AdminCapabilityView map(Capability capability);
}
