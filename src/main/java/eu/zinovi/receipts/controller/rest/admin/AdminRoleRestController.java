package eu.zinovi.receipts.controller.rest.admin;

import eu.zinovi.receipts.domain.model.binding.admin.AdminRoleAddBindingModel;
import eu.zinovi.receipts.domain.model.binding.admin.AdminRoleBindingModel;
import eu.zinovi.receipts.domain.model.binding.admin.AdminRoleDeleteBindingModel;
import eu.zinovi.receipts.domain.model.mapper.AdminRoleAddBindingToService;
import eu.zinovi.receipts.domain.model.mapper.AdminRoleBindingToService;
import eu.zinovi.receipts.domain.model.mapper.AdminRoleDeleteBindingToService;
import eu.zinovi.receipts.domain.model.view.admin.AdminRoleView;
import eu.zinovi.receipts.domain.exception.AccessDeniedException;
import eu.zinovi.receipts.domain.exception.FieldViolationException;
import eu.zinovi.receipts.service.RoleService;
import eu.zinovi.receipts.service.UserService;
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
public class AdminRoleRestController {
    private final AdminRoleDeleteBindingToService adminRoleDeleteBindingToService;
    private final AdminRoleBindingToService adminRoleBindingToService;
    private final AdminRoleAddBindingToService adminRoleAddBindingToService;
    private final UserService userService;
    private final RoleService roleService;

    public AdminRoleRestController(
            AdminRoleDeleteBindingToService adminRoleDeleteBindingToService,
            AdminRoleBindingToService adminRoleBindingToService,
            AdminRoleAddBindingToService adminRoleAddBindingToService,
            UserService userService,
            RoleService roleService) {
        this.adminRoleDeleteBindingToService = adminRoleDeleteBindingToService;
        this.adminRoleBindingToService = adminRoleBindingToService;
        this.adminRoleAddBindingToService = adminRoleAddBindingToService;
        this.userService = userService;
        this.roleService = roleService;
    }

    @RequestMapping(value = "/role", method = RequestMethod.POST,
            consumes = {"application/json"})
    public ResponseEntity<?> addRole(
            @Valid @RequestBody AdminRoleAddBindingModel adminRoleAddBindingModel,
            BindingResult bindingResult) {

        if (!(userService.checkCapability("CAP_ADMIN") && userService.checkCapability("CAP_ADD_ROLE"))) {
            throw new AccessDeniedException(NO_PERMISSION_ROLE_ADD);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        roleService.addRole(adminRoleAddBindingToService
                .map(adminRoleAddBindingModel));

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/role/all", method = RequestMethod.GET,
            produces = {"application/json"})
    public ResponseEntity<List<AdminRoleView>> listRoles() {

        if (!(userService.checkCapability("CAP_ADMIN") && userService.checkCapability("CAP_LIST_ROLES"))) {
            throw new AccessDeniedException(NO_PERMISSION_ROLE_LIST);
        }

        return ResponseEntity.ok(roleService.listRoles());
    }

    @RequestMapping(value = "/role", method = RequestMethod.PUT,
            consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<?> saveRole(
            @Valid @RequestBody AdminRoleBindingModel adminRoleBindingModel,
            BindingResult bindingResult) {

        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_EDIT_ROLE")) {
            throw new AccessDeniedException(NO_PERMISSION_ROLE_EDIT);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        roleService.updateRole(adminRoleBindingToService
                .map(adminRoleBindingModel));

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/role", method = RequestMethod.DELETE,
            consumes = "application/json")
    public ResponseEntity<?> deleteRole(
            @Valid @RequestBody AdminRoleDeleteBindingModel adminRoleDeleteBindingModel,
            BindingResult bindingResult) {

        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_DELETE_ROLE")) {
            throw new AccessDeniedException(NO_PERMISSION_ROLE_DELETE);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        roleService.deleteRole(adminRoleDeleteBindingToService
                .map(adminRoleDeleteBindingModel));

        return ResponseEntity.ok().build();
    }
}
