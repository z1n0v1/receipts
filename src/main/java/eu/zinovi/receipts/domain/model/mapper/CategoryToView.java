package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.entity.Category;
import eu.zinovi.receipts.domain.model.view.CategoryView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryToView {

        CategoryView map(Category category);
}
