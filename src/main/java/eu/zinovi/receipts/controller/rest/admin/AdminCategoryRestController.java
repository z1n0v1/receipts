package eu.zinovi.receipts.controller.rest.admin;

import eu.zinovi.receipts.domain.model.binding.admin.AdminCategoryAddBindingModel;
import eu.zinovi.receipts.domain.model.binding.admin.AdminCategoryDeleteBindingModel;
import eu.zinovi.receipts.domain.model.binding.admin.AdminCategorySaveBindingModel;
import eu.zinovi.receipts.domain.model.mapper.AdminCategoryAddBindingToService;
import eu.zinovi.receipts.domain.model.mapper.AdminCategoryDeleteBindingToService;
import eu.zinovi.receipts.domain.model.mapper.AdminCategorySaveBindingToService;
import eu.zinovi.receipts.domain.model.view.admin.AdminCategoryView;
import eu.zinovi.receipts.domain.exception.AccessDeniedException;
import eu.zinovi.receipts.domain.exception.FieldViolationException;
import eu.zinovi.receipts.service.CategoryService;
import eu.zinovi.receipts.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminCategoryRestController {
    private final AdminCategoryDeleteBindingToService adminCategoryDeleteBindingToService;
    private final AdminCategoryAddBindingToService adminCategoryAddBindingToService;
    private final AdminCategorySaveBindingToService adminCategorySaveBindingToService;
    private final UserService userService;
    private final CategoryService categoryService;

    public AdminCategoryRestController(AdminCategoryDeleteBindingToService adminCategoryDeleteBindingToService, AdminCategoryAddBindingToService adminCategoryAddBindingToService, AdminCategorySaveBindingToService adminCategorySaveBindingToService, UserService userService, CategoryService categoryService) {
        this.adminCategoryDeleteBindingToService = adminCategoryDeleteBindingToService;
        this.adminCategoryAddBindingToService = adminCategoryAddBindingToService;
        this.adminCategorySaveBindingToService = adminCategorySaveBindingToService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @RequestMapping(value = "/category", method = RequestMethod.POST,
            consumes = "application/json")
    public ResponseEntity<?> addCategory(
            @Valid @RequestBody AdminCategoryAddBindingModel adminCategoryAddBindingModel,
            BindingResult bindingResult) {

        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_ADD_CATEGORY")) {
            throw new AccessDeniedException("Нямате право да добавяте категории");
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        categoryService.addCategory(adminCategoryAddBindingToService
                .map(adminCategoryAddBindingModel));

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/category/all", method = RequestMethod.GET,
            consumes = "application/json")
    public ResponseEntity<List<AdminCategoryView>> allCategories() {

        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_LIST_CATEGORIES")) {
            throw new AccessDeniedException("Нямате право да разглеждате списъка с категории");
        }

        return ResponseEntity.ok(categoryService.getAdminAllCategories());
    }

    @RequestMapping(value = "/category", method = RequestMethod.PUT,
            consumes = "application/json")
    public ResponseEntity<?> saveCategory(
            @Valid @RequestBody AdminCategorySaveBindingModel adminCategorySaveBindingModel,
            BindingResult bindingResult) {

        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_EDIT_CATEGORY")) {
            throw new AccessDeniedException("Нямате право да редактирате категориите");
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        categoryService.saveCategory(adminCategorySaveBindingToService
                .map(adminCategorySaveBindingModel));

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/category", method = RequestMethod.DELETE,
            consumes = {"application/json"})
    public ResponseEntity<?> deleteCategory(
            @Valid @RequestBody AdminCategoryDeleteBindingModel adminUserDeleteBindingModel,
            BindingResult bindingResult) {

        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_DELETE_CATEGORY")) {
            throw new AccessDeniedException("Нямате право да триете категории");
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        categoryService.deleteCategory(adminCategoryDeleteBindingToService
                .map(adminUserDeleteBindingModel));

        return ResponseEntity.ok().build();
    }
}
