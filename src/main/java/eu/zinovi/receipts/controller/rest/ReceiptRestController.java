package eu.zinovi.receipts.controller.rest;

import eu.zinovi.receipts.domain.model.binding.receipt.ReceiptDeleteBindingModel;
import eu.zinovi.receipts.domain.model.binding.receipt.ReceiptEditBindingModel;
import eu.zinovi.receipts.domain.model.datatable.FromDatatable;
import eu.zinovi.receipts.domain.model.datatable.ToDatatable;
import eu.zinovi.receipts.domain.model.mapper.ReceiptDeleteBindingToService;
import eu.zinovi.receipts.domain.model.mapper.ReceiptEditBindingToService;
import eu.zinovi.receipts.domain.exception.AccessDeniedException;
import eu.zinovi.receipts.domain.exception.FieldViolationException;
import eu.zinovi.receipts.domain.exception.ReceiptProcessException;
import eu.zinovi.receipts.service.MessagingService;
import eu.zinovi.receipts.service.ReceiptsService;
import eu.zinovi.receipts.service.UserService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@RestController
@RequestMapping("/api")
public class ReceiptRestController {
    private final ReceiptDeleteBindingToService receiptDeleteBindingToService;
    private final ReceiptEditBindingToService receiptEditBindingToService;
    private final ReceiptsService receiptsService;
    private final UserService userService;
    private final MessagingService messagingService;

    public ReceiptRestController(ReceiptDeleteBindingToService receiptDeleteBindingToService, ReceiptEditBindingToService receiptEditBindingToService, ReceiptsService receiptsService, UserService userService, MessagingService messagingService) {
        this.receiptDeleteBindingToService = receiptDeleteBindingToService;
        this.receiptEditBindingToService = receiptEditBindingToService;
        this.receiptsService = receiptsService;
        this.userService = userService;
        this.messagingService = messagingService;
    }

    @RequestMapping(value = "/receipt", method = RequestMethod.POST,
            consumes = "multipart/form-data")
    public ResponseEntity<Set<UUID>> uploadReceipt(
            @RequestParam("file") MultipartFile[] files) {

        if (!userService.checkCapability("CAP_ADD_RECEIPT")) {
            throw new AccessDeniedException(NO_PERMISSION_RECEIPT_ADD);
        }

        Set<UUID> receiptUuids = new HashSet<>();
        messagingService.sendMessage("Обработка...");
        for (MultipartFile file : files) {
            try {
                receiptUuids.add(receiptsService.uploadReceipt(file));

                System.gc(); // Needed for memory cleanup after the image processing
            } catch (ReceiptProcessException ex) {
                messagingService.sendMessage(file.getOriginalFilename() + ": " + ex.getMessage(), "danger");
            }
        }
        System.gc(); // Needed for memory cleanup after the image processing
//        receiptProcessApi.close();

        if (receiptUuids.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(receiptUuids);
    }

    @RequestMapping(value = "/receipt/list", method = RequestMethod.POST,
            consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<ToDatatable> getReceipts(
            @Valid @RequestBody FromDatatable fromDatatable,
            BindingResult bindingResult) {

        if (!userService.checkCapability("CAP_LIST_RECEIPTS") &&
                !userService.checkCapability("CAP_LIST_ALL_RECEIPTS")) {
            throw new AccessDeniedException(NO_PERMISSION_RECEIPT_VIEW);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        if (userService.checkCapability("CAP_LIST_ALL_RECEIPTS")) {
            return ResponseEntity.ok(receiptsService.getAllReceipts(fromDatatable));
        } else {
            return ResponseEntity.ok(receiptsService.getReceipts(fromDatatable));
        }
    }

    @RequestMapping(value = "/receipt", method = RequestMethod.PUT,
            consumes = {"application/json"})
    public ResponseEntity<?> saveReceipt(
            @Valid @RequestBody ReceiptEditBindingModel receiptEditBindingModel,
            BindingResult bindingResult) {

        if (!userService.checkCapability("CAP_EDIT_RECEIPT")) {
            throw new AccessDeniedException(NO_PERMISSION_RECEIPT_EDIT);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        receiptsService.saveReceipt(
                receiptEditBindingToService.map(receiptEditBindingModel));

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/receipt", method = RequestMethod.DELETE,
            consumes = {"application/json"})
    public ResponseEntity<?> deleteReceipt(
            @Valid @RequestBody ReceiptDeleteBindingModel receiptDeleteBindingModel,
            BindingResult bindingResult) {

        if (!userService.checkCapability("CAP_DELETE_RECEIPT")) {
            throw new AccessDeniedException(NO_PERMISSION_RECEIPT_DELETE);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        receiptsService.deleteReceipt(
                receiptDeleteBindingToService.map(receiptDeleteBindingModel));

        return ResponseEntity.ok().build();
    }
}
