package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.entity.Item;
import eu.zinovi.receipts.domain.model.service.ItemAddServiceModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemAddServiceToItem {
//    @Mapping(source = "category", target = "category.name")
    @Mapping(target = "category", ignore = true)
    Item map(ItemAddServiceModel itemAddServiceModel);
}
