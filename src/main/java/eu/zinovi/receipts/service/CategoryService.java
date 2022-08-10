package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.entity.Category;
import eu.zinovi.receipts.domain.model.service.AdminCategoryAddServiceModel;
import eu.zinovi.receipts.domain.model.service.AdminCategoryDeleteServiceModel;
import eu.zinovi.receipts.domain.model.service.AdminCategorySaveServiceModel;
import eu.zinovi.receipts.domain.model.view.CategoryView;
import eu.zinovi.receipts.domain.model.view.admin.AdminCategoryView;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Optional<Category> findByName(String name);

    List<CategoryView> getAllCategories();

    @Transactional
    List<AdminCategoryView> getAdminAllCategories();

    void saveCategory(AdminCategorySaveServiceModel adminCategorySaveServiceModel);

    void addCategory(AdminCategoryAddServiceModel adminCategoryAddServiceModel);

    void deleteCategory(AdminCategoryDeleteServiceModel adminCategoryDeleteServiceModel);

    boolean existsByName(String category);
}
