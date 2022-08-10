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
import eu.zinovi.receipts.service.impl.MessagingServiceImpl;
import eu.zinovi.receipts.service.impl.ReceiptsServiceImpl;
import eu.zinovi.receipts.service.impl.UserServiceImpl;
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
    private final ReceiptsServiceImpl receiptsServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final MessagingServiceImpl messagingServiceImpl;

    public ReceiptRestController(ReceiptDeleteBindingToService receiptDeleteBindingToService, ReceiptEditBindingToService receiptEditBindingToService, ReceiptsServiceImpl receiptsServiceImpl, UserServiceImpl userServiceImpl, MessagingServiceImpl messagingServiceImpl) {
        this.receiptDeleteBindingToService = receiptDeleteBindingToService;
        this.receiptEditBindingToService = receiptEditBindingToService;
        this.receiptsServiceImpl = receiptsServiceImpl;
        this.userServiceImpl = userServiceImpl;
        this.messagingServiceImpl = messagingServiceImpl;
    }

    @RequestMapping(value = "/receipt", method = RequestMethod.POST,
            consumes = "multipart/form-data")
    public ResponseEntity<Set<UUID>> uploadReceipt(
            @RequestParam("file") MultipartFile[] files) {

        if (!userServiceImpl.checkCapability("CAP_ADD_RECEIPT")) {
            throw new AccessDeniedException(NO_PERMISSION_RECEIPT_ADD);
        }

        Set<UUID> receiptUuids = new HashSet<>();
        messagingServiceImpl.sendMessage("Обработка...");
        for (MultipartFile file : files) {
            try {
                receiptUuids.add(receiptsServiceImpl.uploadReceipt(file));

                System.gc(); // Needed for memory cleanup after the image processing
            } catch (ReceiptProcessException ex) {
                messagingServiceImpl.sendMessage(file.getOriginalFilename() + ": " + ex.getMessage(), "danger");
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

        if (!userServiceImpl.checkCapability("CAP_LIST_RECEIPTS") &&
                !userServiceImpl.checkCapability("CAP_LIST_ALL_RECEIPTS")) {
            throw new AccessDeniedException(NO_PERMISSION_RECEIPT_VIEW);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        if (userServiceImpl.checkCapability("CAP_LIST_ALL_RECEIPTS")) {
            return ResponseEntity.ok(receiptsServiceImpl.getAllReceipts(fromDatatable));
        } else {
            return ResponseEntity.ok(receiptsServiceImpl.getReceipts(fromDatatable));
        }
    }

    @RequestMapping(value = "/receipt", method = RequestMethod.PUT,
            consumes = {"application/json"})
    public ResponseEntity<?> saveReceipt(
            @Valid @RequestBody ReceiptEditBindingModel receiptEditBindingModel,
            BindingResult bindingResult) {

        if (!userServiceImpl.checkCapability("CAP_EDIT_RECEIPT")) {
            throw new AccessDeniedException(NO_PERMISSION_RECEIPT_EDIT);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        receiptsServiceImpl.saveReceipt(
                receiptEditBindingToService.map(receiptEditBindingModel));

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/receipt", method = RequestMethod.DELETE,
            consumes = {"application/json"})
    public ResponseEntity<?> deleteReceipt(
            @Valid @RequestBody ReceiptDeleteBindingModel receiptDeleteBindingModel,
            BindingResult bindingResult) {

        if (!userServiceImpl.checkCapability("CAP_DELETE_RECEIPT")) {
            throw new AccessDeniedException(NO_PERMISSION_RECEIPT_DELETE);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        receiptsServiceImpl.deleteReceipt(
                receiptDeleteBindingToService.map(receiptDeleteBindingModel));

        return ResponseEntity.ok().build();
    }
}
