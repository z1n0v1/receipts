package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.item.ItemDeleteBindingModel;
import eu.zinovi.receipts.domain.model.service.ItemDeleteServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemDeleteBindingToService {
    ItemDeleteServiceModel map(ItemDeleteBindingModel itemDeleteBindingModel);
}
