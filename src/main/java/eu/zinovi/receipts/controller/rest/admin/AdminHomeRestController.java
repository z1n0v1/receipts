package eu.zinovi.receipts.controller.rest.admin;

import eu.zinovi.receipts.domain.model.datatable.FromDatatable;
import eu.zinovi.receipts.domain.model.datatable.ToDatatable;
import eu.zinovi.receipts.domain.model.view.admin.AdminCapabilityView;
import eu.zinovi.receipts.domain.exception.AccessDeniedException;
import eu.zinovi.receipts.domain.exception.FieldViolationException;
import eu.zinovi.receipts.service.AdminService;
import eu.zinovi.receipts.service.UserService;
import eu.zinovi.receipts.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static eu.zinovi.receipts.util.constants.MessageConstants.NO_PERMISSION_CAPABILITY_LIST;
import static eu.zinovi.receipts.util.constants.MessageConstants.NO_PERMISSION_RECEIPT_LIST;

@RestController
@RequestMapping("/api/admin")
public class AdminHomeRestController {
    private final AdminService adminService;
    private final UserService userService;

    public AdminHomeRestController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    @RequestMapping(value = "/receipt/all", method = RequestMethod.POST,
            consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<ToDatatable> receiptsList(
            @Valid @RequestBody FromDatatable fromDatatable,
            BindingResult bindingResult) {

        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_ADMIN_LIST_RECEIPTS")) {
            throw new AccessDeniedException(NO_PERMISSION_RECEIPT_LIST);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        return ResponseEntity.ok(adminService.listReceipts(fromDatatable));

    }

    @RequestMapping(value = "/capability/all", method = RequestMethod.GET,
            produces = {"application/json"})
    public ResponseEntity<List<AdminCapabilityView>> getCapabilities() {

        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_LIST_CAPABILITIES")) {
            throw new AccessDeniedException(NO_PERMISSION_CAPABILITY_LIST);
        }

        return ResponseEntity.ok(adminService.getCapabilities());
    }
}
