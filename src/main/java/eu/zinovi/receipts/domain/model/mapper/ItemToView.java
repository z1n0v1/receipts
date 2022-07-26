package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.entity.Item;
import eu.zinovi.receipts.domain.model.view.ItemView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ItemToView {

    @Mappings({
//            @Mapping(source = "id", target = "itemId"),
            @Mapping(source = "category.name", target = "category")
    })
    ItemView map(Item item);
}
