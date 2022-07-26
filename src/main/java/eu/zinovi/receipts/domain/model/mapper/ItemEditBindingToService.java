package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.binding.item.ItemEditBindingModel;
import eu.zinovi.receipts.domain.model.service.ItemEditServiceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemEditBindingToService {
    ItemEditServiceModel map(ItemEditBindingModel itemEditBindingModel);
}
