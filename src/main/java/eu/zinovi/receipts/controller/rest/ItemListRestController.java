package eu.zinovi.receipts.controller.rest;

import eu.zinovi.receipts.domain.model.binding.item.ItemAddBindingModel;
import eu.zinovi.receipts.domain.model.binding.item.ItemDeleteBindingModel;
import eu.zinovi.receipts.domain.model.binding.item.ItemEditBindingModel;
import eu.zinovi.receipts.domain.model.datatable.FromDatatable;
import eu.zinovi.receipts.domain.model.datatable.ToDatatable;
import eu.zinovi.receipts.domain.model.mapper.ItemAddBindingToService;
import eu.zinovi.receipts.domain.model.mapper.ItemDeleteBindingToService;
import eu.zinovi.receipts.domain.model.mapper.ItemEditBindingToService;
import eu.zinovi.receipts.domain.model.view.CategoryView;
import eu.zinovi.receipts.domain.exception.AccessDeniedException;
import eu.zinovi.receipts.domain.exception.FieldViolationException;
import eu.zinovi.receipts.service.impl.CategoryServiceImpl;
import eu.zinovi.receipts.service.impl.ItemServiceImpl;
import eu.zinovi.receipts.service.impl.ReceiptsServiceImpl;
import eu.zinovi.receipts.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@RestController
@RequestMapping("/api")
public class ItemListRestController {
    private final ItemEditBindingToService itemEditBindingToService;
    private final ItemDeleteBindingToService itemDeleteBindingToService;
    private final ItemAddBindingToService itemAddBindingToService;
    private final ItemServiceImpl itemServiceImpl;
    private final CategoryServiceImpl categoryServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final ReceiptsServiceImpl receiptsServiceImpl;

    public ItemListRestController(ItemEditBindingToService itemEditBindingToService,
                                  ItemDeleteBindingToService itemDeleteBindingToService,
                                  ItemAddBindingToService itemAddBindingToService,
                                  ItemServiceImpl itemServiceImpl, CategoryServiceImpl categoryServiceImpl,
                                  UserServiceImpl userServiceImpl, ReceiptsServiceImpl receiptsServiceImpl) {
        this.itemEditBindingToService = itemEditBindingToService;
        this.itemDeleteBindingToService = itemDeleteBindingToService;
        this.itemAddBindingToService = itemAddBindingToService;
        this.itemServiceImpl = itemServiceImpl;
        this.categoryServiceImpl = categoryServiceImpl;
        this.userServiceImpl = userServiceImpl;
        this.receiptsServiceImpl = receiptsServiceImpl;
    }

    @RequestMapping(value = "/item", method = RequestMethod.POST,
            consumes = "application/json")
    public ResponseEntity<?> addItem(
            @Valid @RequestBody ItemAddBindingModel itemAddBindingModel,
            BindingResult bindingResult) {

        if (!userServiceImpl.checkCapability("CAP_ADD_ITEM")) {
            throw new AccessDeniedException(NO_PERMISSION_ITEM_ADD);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        receiptsServiceImpl.addItem(itemAddBindingToService.map(itemAddBindingModel));

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/item/{receiptId}", method = RequestMethod.POST,
            consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<ToDatatable> items(
            @PathVariable UUID receiptId, @Valid @RequestBody FromDatatable request,
            BindingResult bindingResult) {

        if (!userServiceImpl.checkCapability("CAP_LIST_ITEMS")) {
            throw new AccessDeniedException(NO_PERMISSION_RECEIPT_VIEW);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        return ResponseEntity.ok(itemServiceImpl.getItemListDatatable(receiptId, request));
    }

    @RequestMapping(value = "/item", method = RequestMethod.PUT,
            consumes = {"application/json"})
    public ResponseEntity<?> editItem(
            @Valid @RequestBody ItemEditBindingModel itemEditBindingModel,
            BindingResult bindingResult) {

        if (!userServiceImpl.checkCapability("CAP_EDIT_ITEM")) {
            throw new AccessDeniedException(NO_PERMISSION_ITEM_EDIT);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        itemServiceImpl.updateItem(itemEditBindingToService.map(itemEditBindingModel));

        return ResponseEntity.ok().build();

    }

    @RequestMapping(value = "/item", method = RequestMethod.DELETE,
            consumes = "application/json")
    public ResponseEntity<?> deleteItem(
            @Valid @RequestBody ItemDeleteBindingModel itemDeleteBindingModel,
            BindingResult bindingResult) {

        if (!userServiceImpl.checkCapability("CAP_DELETE_ITEM")) {
            throw new AccessDeniedException(NO_PERMISSION_ITEM_DELETE);
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        receiptsServiceImpl.deleteItem(itemDeleteBindingToService.map(itemDeleteBindingModel));

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/category/all", method = RequestMethod.GET,
            produces = {"application/json"})
    public ResponseEntity<List<CategoryView>> getCategories() {

        if (!userServiceImpl.checkCapability("CAP_LIST_ITEMS")) {
            throw new AccessDeniedException("Нямате достъп до списъка с категориите");
        }

        return ResponseEntity.ok(categoryServiceImpl.getAllCategories());
    }
}
