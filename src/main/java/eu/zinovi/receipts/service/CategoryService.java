package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.entity.Category;
import eu.zinovi.receipts.domain.model.mapper.AdminCategoryToView;
import eu.zinovi.receipts.domain.model.mapper.CategoryToView;
import eu.zinovi.receipts.domain.model.service.AdminCategoryAddServiceModel;
import eu.zinovi.receipts.domain.model.service.AdminCategoryDeleteServiceModel;
import eu.zinovi.receipts.domain.model.service.AdminCategorySaveServiceModel;
import eu.zinovi.receipts.domain.model.view.admin.AdminCategoryView;
import eu.zinovi.receipts.domain.model.view.CategoryView;
import eu.zinovi.receipts.domain.exception.EntityNotFoundException;
import eu.zinovi.receipts.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryToView categoryToView;
    private final AdminCategoryToView adminCategoryToView;
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryToView categoryToView, AdminCategoryToView adminCategoryToView, CategoryRepository categoryRepository) {
        this.categoryToView = categoryToView;
        this.adminCategoryToView = adminCategoryToView;
        this.categoryRepository = categoryRepository;
    }

    public Optional<Category> findByName(String name) {
        return categoryRepository.findByNameOrderByNameAsc(name);
    }

    public List<CategoryView> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryToView::map)
                .toList();
    }

    @Transactional
    public List<AdminCategoryView> getAdminAllCategories() {
        return categoryRepository.findAll().stream()
                .map(adminCategoryToView::map)
                .toList();
    }

    public void saveCategory(AdminCategorySaveServiceModel adminCategorySaveServiceModel) {
        Category category = categoryRepository.findById(adminCategorySaveServiceModel.getId())
                .orElseThrow(EntityNotFoundException::new);
        category.setName(adminCategorySaveServiceModel.getName());
        category.setColor(adminCategorySaveServiceModel.getColor());
        categoryRepository.save(category);
    }

    public void addCategory(AdminCategoryAddServiceModel adminCategoryAddServiceModel) {
        Category category = new Category();
        category.setName(adminCategoryAddServiceModel.getName());
        category.setColor(adminCategoryAddServiceModel.getColor());
        categoryRepository.save(category);
    }

    public void deleteCategory(AdminCategoryDeleteServiceModel adminCategoryDeleteServiceModel) {
        Category category = categoryRepository.findById(adminCategoryDeleteServiceModel.getId())
                .orElseThrow(EntityNotFoundException::new);
        categoryRepository.delete(category);
    }

    public boolean existsByName(String category) {
        return categoryRepository.existsByName(category);
    }
}
