package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.item.ItemAddBindingModel;
import eu.zinovi.receipts.domain.model.service.ItemAddServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemAddBindingToService {
    ItemAddServiceModel map(ItemAddBindingModel itemAddBindingModel);
}
