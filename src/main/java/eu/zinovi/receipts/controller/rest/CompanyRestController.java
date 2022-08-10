package eu.zinovi.receipts.controller.rest;

import eu.zinovi.receipts.domain.model.binding.receipt.ReceiptEikBindingModel;
import eu.zinovi.receipts.domain.model.view.ReceiptCompanyByEikView;
import eu.zinovi.receipts.domain.exception.AccessDeniedException;
import eu.zinovi.receipts.domain.exception.FieldViolationException;
import eu.zinovi.receipts.service.CompanyService;
import eu.zinovi.receipts.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static eu.zinovi.receipts.util.constants.MessageConstants.NO_PERMISSION_RECEIPT_EDIT;

@RestController
@RequestMapping("/api/company")
public class CompanyRestController {
    private final UserService userService;
    private final CompanyService companyService;

    public CompanyRestController(UserService userService, CompanyService companyService) {
        this.userService = userService;
        this.companyService = companyService;
    }

    @RequestMapping(value = "/eik", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<ReceiptCompanyByEikView> getByEik(
            @Valid @RequestBody ReceiptEikBindingModel eik,
            BindingResult bindingResult) {

        if (!userService.checkCapability("CAP_EDIT_RECEIPT")) {
            throw new AccessDeniedException(NO_PERMISSION_RECEIPT_EDIT);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        return ResponseEntity.ok(companyService.receiptEikView(eik.getEik()));
    }
}
