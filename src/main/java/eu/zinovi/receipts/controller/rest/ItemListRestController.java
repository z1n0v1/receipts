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
import eu.zinovi.receipts.service.CategoryService;
import eu.zinovi.receipts.service.ItemService;
import eu.zinovi.receipts.service.ReceiptsService;
import eu.zinovi.receipts.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ItemListRestController {
    private final ItemEditBindingToService itemEditBindingToService;
    private final ItemDeleteBindingToService itemDeleteBindingToService;
    private final ItemAddBindingToService itemAddBindingToService;
    private final ItemService itemService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final ReceiptsService receiptsService;

    public ItemListRestController(ItemEditBindingToService itemEditBindingToService,
                                  ItemDeleteBindingToService itemDeleteBindingToService,
                                  ItemAddBindingToService itemAddBindingToService,
                                  ItemService itemService, CategoryService categoryService,
                                  UserService userService, ReceiptsService receiptsService) {
        this.itemEditBindingToService = itemEditBindingToService;
        this.itemDeleteBindingToService = itemDeleteBindingToService;
        this.itemAddBindingToService = itemAddBindingToService;
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.receiptsService = receiptsService;
    }

    @RequestMapping(value = "/item", method = RequestMethod.POST,
            consumes = "application/json")
    public ResponseEntity<?> add(
            @Valid @RequestBody ItemAddBindingModel itemAddBindingModel,
            BindingResult bindingResult) {

        if (!userService.checkCapability("CAP_ADD_ITEM")) {
            throw new AccessDeniedException("Нямате право да добавяте продукти");
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        receiptsService.addItem(itemAddBindingToService.map(itemAddBindingModel));

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/item/{receiptId}", method = RequestMethod.POST,
            consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<ToDatatable> items(
            @PathVariable UUID receiptId, @Valid @RequestBody FromDatatable request,
            BindingResult bindingResult) {

        if (!userService.checkCapability("CAP_LIST_ITEMS")) {
            throw new AccessDeniedException("Нямате право да разглеждате касови бележки");
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        return ResponseEntity.ok(itemService.getItemListDatatable(receiptId, request));
    }

    @RequestMapping(value = "/item", method = RequestMethod.PUT,
            consumes = {"application/json"})
    public ResponseEntity<?> editItem(
            @Valid @RequestBody ItemEditBindingModel itemEditBindingModel,
            BindingResult bindingResult) {

        if (!userService.checkCapability("CAP_EDIT_ITEM")) {
            throw new AccessDeniedException("Нямате право да променяте продукти");
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        itemService.updateItem(itemEditBindingToService.map(itemEditBindingModel));

        return ResponseEntity.ok().build();

    }

    @RequestMapping(value = "/item", method = RequestMethod.DELETE,
            consumes = "application/json")
    public ResponseEntity<?> delete(
            @Valid @RequestBody ItemDeleteBindingModel itemDeleteBindingModel,
            BindingResult bindingResult) {

        if (!userService.checkCapability("CAP_DELETE_ITEM")) {
            throw new AccessDeniedException("Нямате право да триете продукти");
        }
        if (bindingResult.hasErrors()) {
            throw new FieldViolationException(bindingResult.getAllErrors());
        }

        receiptsService.deleteItem(itemDeleteBindingToService.map(itemDeleteBindingModel));

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/category/all", method = RequestMethod.GET,
            produces = {"application/json"})
    public ResponseEntity<List<CategoryView>> getCategories() {

        if (!userService.checkCapability("CAP_LIST_ITEMS")) {
            throw new AccessDeniedException("Нямате достъп до списъка с категориите");
        }

        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}
