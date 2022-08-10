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
import eu.zinovi.receipts.service.impl.CategoryServiceImpl;
import eu.zinovi.receipts.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@RestController
@RequestMapping("/api/admin")
public class AdminCategoryRestController {
    private final AdminCategoryDeleteBindingToService adminCategoryDeleteBindingToService;
    private final AdminCategoryAddBindingToService adminCategoryAddBindingToService;
    private final AdminCategorySaveBindingToService adminCategorySaveBindingToService;
    private final UserServiceImpl userServiceImpl;
    private final CategoryServiceImpl categoryServiceImpl;

    public AdminCategoryRestController(AdminCategoryDeleteBindingToService adminCategoryDeleteBindingToService, AdminCategoryAddBindingToService adminCategoryAddBindingToService, AdminCategorySaveBindingToService adminCategorySaveBindingToService, UserServiceImpl userServiceImpl, CategoryServiceImpl categoryServiceImpl) {
        this.adminCategoryDeleteBindingToService = adminCategoryDeleteBindingToService;
        this.adminCategoryAddBindingToService = adminCategoryAddBindingToService;
        this.adminCategorySaveBindingToService = adminCategorySaveBindingToService;
        this.userServiceImpl = userServiceImpl;
        this.categoryServiceImpl = categoryServiceImpl;
    }

    @RequestMapping(value = "/category", method = RequestMethod.POST,
            consumes = "application/json")
    public ResponseEntity<?> addCategory(
            @Valid @RequestBody AdminCategoryAddBindingModel adminCategoryAddBindingModel,
            BindingResult bindingResult) {

        if (!userServiceImpl.checkCapability("CAP_ADMIN") || !userServiceImpl.checkCapability("CAP_ADD_CATEGORY")) {
            throw new AccessDeniedException(NO_PERMISSION_CATEGORY_ADD);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        categoryServiceImpl.addCategory(adminCategoryAddBindingToService
                .map(adminCategoryAddBindingModel));

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/category/all", method = RequestMethod.GET,
            consumes = "application/json")
    public ResponseEntity<List<AdminCategoryView>> allCategories() {

        if (!userServiceImpl.checkCapability("CAP_ADMIN") || !userServiceImpl.checkCapability("CAP_LIST_CATEGORIES")) {
            throw new AccessDeniedException(NO_PERMISSION_CATEGORY_LIST);
        }

        return ResponseEntity.ok(categoryServiceImpl.getAdminAllCategories());
    }

    @RequestMapping(value = "/category", method = RequestMethod.PUT,
            consumes = "application/json")
    public ResponseEntity<?> saveCategory(
            @Valid @RequestBody AdminCategorySaveBindingModel adminCategorySaveBindingModel,
            BindingResult bindingResult) {

        if (!userServiceImpl.checkCapability("CAP_ADMIN") || !userServiceImpl.checkCapability("CAP_EDIT_CATEGORY")) {
            throw new AccessDeniedException(NO_PERMISSION_CATEGORY_EDIT);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        categoryServiceImpl.saveCategory(adminCategorySaveBindingToService
                .map(adminCategorySaveBindingModel));

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/category", method = RequestMethod.DELETE,
            consumes = {"application/json"})
    public ResponseEntity<?> deleteCategory(
            @Valid @RequestBody AdminCategoryDeleteBindingModel adminUserDeleteBindingModel,
            BindingResult bindingResult) {

        if (!userServiceImpl.checkCapability("CAP_ADMIN") || !userServiceImpl.checkCapability("CAP_DELETE_CATEGORY")) {
            throw new AccessDeniedException(NO_PERMISSION_CATEGORY_DELETE);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        categoryServiceImpl.deleteCategory(adminCategoryDeleteBindingToService
                .map(adminUserDeleteBindingModel));

        return ResponseEntity.ok().build();
    }
}
