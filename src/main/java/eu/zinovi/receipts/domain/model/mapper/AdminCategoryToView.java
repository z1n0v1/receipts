package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.entity.Category;
import eu.zinovi.receipts.domain.model.view.admin.AdminCategoryView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AdminCategoryToView {

    @Mappings({
//        @Mapping(source = "id", target = "id"),
//        @Mapping(source = "name", target = "name"),
//        @Mapping(source = "color", target = "color"),
        @Mapping(source = "category", target = "itemsCount", qualifiedByName = "getItemsCount")
    })
    AdminCategoryView map(Category category);

    @Named("getItemsCount")
    default int getItemsCount(Category category) {
        return category.getItems().size();
    }
}
