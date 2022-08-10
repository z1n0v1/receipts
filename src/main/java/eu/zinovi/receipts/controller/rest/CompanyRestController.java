package eu.zinovi.receipts.controller.rest;

import eu.zinovi.receipts.domain.model.binding.receipt.ReceiptEikBindingModel;
import eu.zinovi.receipts.domain.model.view.ReceiptCompanyByEikView;
import eu.zinovi.receipts.domain.exception.AccessDeniedException;
import eu.zinovi.receipts.domain.exception.FieldViolationException;
import eu.zinovi.receipts.service.impl.CompanyServiceImpl;
import eu.zinovi.receipts.service.impl.UserServiceImpl;
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
    private final UserServiceImpl userServiceImpl;
    private final CompanyServiceImpl companyServiceImpl;

    public CompanyRestController(UserServiceImpl userServiceImpl, CompanyServiceImpl companyServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        this.companyServiceImpl = companyServiceImpl;
    }

    @RequestMapping(value = "/eik", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<ReceiptCompanyByEikView> getByEik(
            @Valid @RequestBody ReceiptEikBindingModel eik,
            BindingResult bindingResult) {

        if (!userServiceImpl.checkCapability("CAP_EDIT_RECEIPT")) {
            throw new AccessDeniedException(NO_PERMISSION_RECEIPT_EDIT);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        return ResponseEntity.ok(companyServiceImpl.receiptEikView(eik.getEik()));
    }
}
