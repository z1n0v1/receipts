package eu.zinovi.receipts.controller.rest.admin;

import eu.zinovi.receipts.domain.model.binding.admin.AdminGetUserDetailsBindingModel;
import eu.zinovi.receipts.domain.model.binding.admin.AdminUserDeleteBindingModel;
import eu.zinovi.receipts.domain.model.binding.admin.AdminUserSaveBindingModel;
import eu.zinovi.receipts.domain.model.datatable.FromDatatable;
import eu.zinovi.receipts.domain.model.datatable.ToDatatable;
import eu.zinovi.receipts.domain.model.mapper.AdminUserDeleteBindingToService;
import eu.zinovi.receipts.domain.model.mapper.AdminUserSaveBindingToService;
import eu.zinovi.receipts.domain.model.view.admin.AdminUserView;
import eu.zinovi.receipts.domain.exception.AccessDeniedException;
import eu.zinovi.receipts.domain.exception.FieldViolationException;
import eu.zinovi.receipts.service.AdminService;
import eu.zinovi.receipts.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminUserRestController {

    private final AdminUserSaveBindingToService adminUserSaveBindingToService;
    private final AdminUserDeleteBindingToService adminUserDeleteBindingToService;

    private final UserService userService;
    private final AdminService adminService;

    public AdminUserRestController(AdminUserSaveBindingToService adminUserSaveBindingToService, AdminUserDeleteBindingToService adminUserDeleteBindingToService, UserService userService, AdminService adminService) {
        this.adminUserSaveBindingToService = adminUserSaveBindingToService;
        this.adminUserDeleteBindingToService = adminUserDeleteBindingToService;
        this.userService = userService;
        this.adminService = adminService;
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST,
            consumes = ("application/json"), produces = {"application/json"})
    public ResponseEntity<AdminUserView> getUserDetails(
            @Valid @RequestBody AdminGetUserDetailsBindingModel adminGetUserDetailsBindingModel,
            BindingResult bindingResult) {

        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_LIST_USERS")) {
            throw new AccessDeniedException("Нямате право да разглеждате детайлите на потребителя");
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        return ResponseEntity.ok(adminService.getUserDetails(
                adminGetUserDetailsBindingModel.getEmail()));
    }

    @RequestMapping(value = "/user/all", method = RequestMethod.POST,
            consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<ToDatatable> usersList(
            @Valid @RequestBody FromDatatable fromDatatable,
            BindingResult bindingResult) {

        if (!(userService.checkCapability("CAP_ADMIN") && userService.checkCapability("CAP_LIST_USERS"))) {
            throw new AccessDeniedException("Нямате право да разглеждате списъка с потребители");
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        return ResponseEntity.ok(adminService.listUsers(fromDatatable));
    }

    @RequestMapping(value = "/user", method = RequestMethod.PUT,
            consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<?> saveUserDetails(
            @Valid @RequestBody AdminUserSaveBindingModel adminUserSaveBindingModel,
            BindingResult bindingResult) {

        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_EDIT_ALL_USERS")) {
            throw new AccessDeniedException("Нямате право да редактирате детайлите на потребителя");
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        adminService.updateUser(adminUserSaveBindingToService
                .map(adminUserSaveBindingModel));

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/user", method = RequestMethod.DELETE,
            consumes = {"application/json"})
    public ResponseEntity<?> deleteUserDetails(
            @Valid @RequestBody AdminUserDeleteBindingModel adminUserDeleteBindingModel,
            BindingResult bindingResult) {

        if (!(userService.checkCapability("CAP_ADMIN") && userService.checkCapability("CAP_DELETE_USER"))) {
            throw new AccessDeniedException("Нямате право да триете потребители");
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        userService.deleteUserByEmail(adminUserDeleteBindingToService
                .map(adminUserDeleteBindingModel));

        return ResponseEntity.ok().build();
    }



}
