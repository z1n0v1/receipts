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
import eu.zinovi.receipts.service.impl.AdminServiceImpl;
import eu.zinovi.receipts.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@RestController
@RequestMapping("/api/admin")
public class AdminUserRestController {

    private final AdminUserSaveBindingToService adminUserSaveBindingToService;
    private final AdminUserDeleteBindingToService adminUserDeleteBindingToService;

    private final UserServiceImpl userServiceImpl;
    private final AdminServiceImpl adminServiceImpl;

    public AdminUserRestController(AdminUserSaveBindingToService adminUserSaveBindingToService, AdminUserDeleteBindingToService adminUserDeleteBindingToService, UserServiceImpl userServiceImpl, AdminServiceImpl adminServiceImpl) {
        this.adminUserSaveBindingToService = adminUserSaveBindingToService;
        this.adminUserDeleteBindingToService = adminUserDeleteBindingToService;
        this.userServiceImpl = userServiceImpl;
        this.adminServiceImpl = adminServiceImpl;
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST,
            consumes = ("application/json"), produces = {"application/json"})
    public ResponseEntity<AdminUserView> getUserDetails(
            @Valid @RequestBody AdminGetUserDetailsBindingModel adminGetUserDetailsBindingModel,
            BindingResult bindingResult) {

        if (!userServiceImpl.checkCapability("CAP_ADMIN") || !userServiceImpl.checkCapability("CAP_LIST_USERS")) {
            throw new AccessDeniedException(NO_PERMISSION_USER_DETAILS);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        return ResponseEntity.ok(adminServiceImpl.getUserDetails(
                adminGetUserDetailsBindingModel.getEmail()));
    }

    @RequestMapping(value = "/user/all", method = RequestMethod.POST,
            consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<ToDatatable> usersList(
            @Valid @RequestBody FromDatatable fromDatatable,
            BindingResult bindingResult) {

        if (!(userServiceImpl.checkCapability("CAP_ADMIN") && userServiceImpl.checkCapability("CAP_LIST_USERS"))) {
            throw new AccessDeniedException(NO_PERMISSION_USER_LIST);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        return ResponseEntity.ok(adminServiceImpl.listUsers(fromDatatable));
    }

    @RequestMapping(value = "/user", method = RequestMethod.PUT,
            consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<?> saveUserDetails(
            @Valid @RequestBody AdminUserSaveBindingModel adminUserSaveBindingModel,
            BindingResult bindingResult) {

        if (!userServiceImpl.checkCapability("CAP_ADMIN") || !userServiceImpl.checkCapability("CAP_EDIT_ALL_USERS")) {
            throw new AccessDeniedException(NO_PERMISSION_USER_EDIT);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        adminServiceImpl.updateUser(adminUserSaveBindingToService
                .map(adminUserSaveBindingModel));

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/user", method = RequestMethod.DELETE,
            consumes = {"application/json"})
    public ResponseEntity<?> deleteUserDetails(
            @Valid @RequestBody AdminUserDeleteBindingModel adminUserDeleteBindingModel,
            BindingResult bindingResult) {

        if (!(userServiceImpl.checkCapability("CAP_ADMIN") && userServiceImpl.checkCapability("CAP_DELETE_USER"))) {
            throw new AccessDeniedException(NO_PERMISSION_USER_DELETE);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        userServiceImpl.deleteUserByEmail(adminUserDeleteBindingToService
                .map(adminUserDeleteBindingModel));

        return ResponseEntity.ok().build();
    }



}
