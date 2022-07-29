package eu.zinovi.receipts.controller.rest;

import com.google.gson.Gson;
import eu.zinovi.receipts.WithMockEmailUser;
import eu.zinovi.receipts.domain.model.binding.item.ItemAddBindingModel;
import eu.zinovi.receipts.domain.model.binding.item.ItemDeleteBindingModel;
import eu.zinovi.receipts.domain.model.binding.item.ItemEditBindingModel;
import eu.zinovi.receipts.domain.model.datatable.DatatableColumn;
import eu.zinovi.receipts.domain.model.datatable.DatatableOrder;
import eu.zinovi.receipts.domain.model.datatable.DatatableSearch;
import eu.zinovi.receipts.domain.model.datatable.FromDatatable;
import eu.zinovi.receipts.domain.model.entity.*;
import eu.zinovi.receipts.repository.*;
import eu.zinovi.receipts.service.ItemService;
import eu.zinovi.receipts.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class ItemListControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    Gson gson;

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ReceiptRepository receiptRepository;
    @Autowired
    private ReceiptImageRepository receiptImageRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserService userService;
    @Autowired
    ItemService itemService;

    private Receipt receipt;

    @BeforeEach
    void setUp() {
        Address address = new Address("address");
        addressRepository.save(address);
        Company company = new Company("123456789", "company", "activity", address);
        companyRepository.save(company);
        Store store = new Store("store", address, company);
        storeRepository.save(store);
        ReceiptImage receiptImage = new ReceiptImage(
                "imageUrl",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                false,
                null,
                userService.getCurrentUser()
        );
        receiptImageRepository.save(receiptImage);
        receipt = new Receipt(
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                BigDecimal.valueOf(100),
                "receipt lines",
                true,
                company,
                store,
                new ArrayList<>(),
                BigDecimal.valueOf(100),
                userService.getCurrentUser(),
                receiptImage
        );
        receiptRepository.save(receipt);

        Category category = new Category("category", "#FFFFFF");
        categoryRepository.save(category);

        Item item = new Item(
                "name",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                1,
                category,
                receipt
        );
        itemRepository.save(item);
        receipt.getItems().add(item);

        item = new Item(
                "name",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                2,
                category,
                receipt
        );
        itemRepository.save(item);
        receipt.getItems().add(item);

        item = new Item(
                "name",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                3,
                category,
                receipt
        );
        itemRepository.save(item);
        receipt.getItems().add(item);


        receiptRepository.save(receipt);
    }

    @Test
    @WithMockEmailUser
    public void addWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/item")
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(new ItemAddBindingModel(
                                UUID.randomUUID().toString(),
                                "Други",
                                "Item",
                                BigDecimal.valueOf(1.5),
                                BigDecimal.valueOf(1.5)
                        )))
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        "Нямате право да добавяте продукти"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADD_ITEM"})
    public void addValidWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/item")
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(new ItemAddBindingModel(
                                receipt.getId().toString(),
                                "Други",
                                "Item",
                                BigDecimal.valueOf(1.5),
                                BigDecimal.valueOf(1.5)
                        )))
                )
                .andExpect(status().isOk());

        Item newItem = itemService.getItems(receipt.getId()).stream()
                .filter(i -> i.getName().equals("Item") &&
                        i.getCategory().getName().equals("Други") &&
                        i.getPrice().equals(BigDecimal.valueOf(1.5)) &&
                        i.getQuantity().equals(BigDecimal.valueOf(1.5))).findAny().orElse(null);

        Assertions.assertNotNull(newItem);
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADD_ITEM"})
    public void addInvalidWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/item")
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(new ItemAddBindingModel(
                                receipt.getId().toString(),
                                "No Such Category",
                                "Item",
                                BigDecimal.valueOf(1.5),
                                BigDecimal.valueOf(1.5)
                        )))
                )
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        "Категорията не съществува\n"));
    }

    @Test
    @WithMockEmailUser
    public void itemsTableWithoutCap() throws Exception {
        FromDatatable fromDatatable = new FromDatatable(
                1L,
                0,
                10,
                null,
                null,
                null,
                new DatatableColumn[]{
                        new DatatableColumn("0", "position", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("1", "category", "true", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("2", "name", "true", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("3", "quantity", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("4", "price", "false", "true",
                                new DatatableSearch("", "false")),
                },
                new DatatableOrder[]{
                        new DatatableOrder("0", "asc")
                },
                new DatatableSearch("", "false")
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/item/{id}", receipt.getId())
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(fromDatatable)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        "Нямате право да разглеждате касови бележки"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_LIST_ITEMS"})
    public void itemsTableInvalidWithCap() throws Exception {

        FromDatatable fromDatatable = new FromDatatable(
                null,
                0,
                10,
                null,
                null,
                null,
                new DatatableColumn[]{
                        new DatatableColumn("0", "position", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("1", "category", "true", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("2", "name", "true", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("3", "quantity", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("4", "price", "false", "true",
                                new DatatableSearch("", "false")),
                },
                new DatatableOrder[]{
                        new DatatableOrder("0", "asc")
                },
                new DatatableSearch("", "false")
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/item/{id}", receipt.getId())
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(fromDatatable)))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        "Трябва да има идентификатор на заявката\n"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_LIST_ITEMS"})
    public void itemsTableValidWithCap() throws Exception {
        FromDatatable fromDatatable = new FromDatatable(
                0L,
                0,
                10,
                null,
                null,
                null,
                new DatatableColumn[]{
                        new DatatableColumn("0", "position", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("1", "category", "true", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("2", "name", "true", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("3", "quantity", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("4", "price", "false", "true",
                                new DatatableSearch("", "false")),
                },
                new DatatableOrder[]{
                        new DatatableOrder("0", "asc")
                },
                new DatatableSearch("", "false")
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/item/{id}", receipt.getId())
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(fromDatatable)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @WithMockEmailUser
    public void editItemWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/item")
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(new ItemEditBindingModel(
                                receipt.getId().toString(),
                                1,
                                "Други",
                                "Item",
                                BigDecimal.valueOf(0.5),
                                BigDecimal.valueOf(0.5)
                        )))
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        "Нямате право да променяте продукти"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_EDIT_ITEM"})
    public void editItemInvalidWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/item")
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(new ItemEditBindingModel(
                                null,
                                1,
                                "Други",
                                "Item",
                                BigDecimal.valueOf(0.5),
                                BigDecimal.valueOf(0.5)
                        )))
                )
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        "Касовата бележка не съществува\nНе сте подали номер на касовата бележка\n"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_EDIT_ITEM"})
    public void editItemValidWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/item")
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(new ItemEditBindingModel(
                                receipt.getId().toString(),
                                1,
                                "Други",
                                "Item",
                                BigDecimal.valueOf(0.5),
                                BigDecimal.valueOf(0.5
                                )))
                        ))
                .andExpect(status().isOk());

        Item newItem = itemService.getItems(receipt.getId()).stream()
                .filter(i -> i.getName().equals("Item") &&
                        i.getCategory().getName().equals("Други") &&
                        i.getPrice().equals(BigDecimal.valueOf(0.5)) &&
                        i.getQuantity().equals(BigDecimal.valueOf(0.5))).findAny().orElse(null);

        Assertions.assertNotNull(newItem);
    }

    @Test
    @WithMockEmailUser
    public void deleteItemWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/item")
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(new ItemDeleteBindingModel(
                                receipt.getId().toString(),
                                1
                        )))
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        "Нямате право да триете артикули"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_DELETE_ITEM"})
    public void deleteItemInvalidWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/item")
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(new ItemDeleteBindingModel(
                                null,
                                1
                        )))
                )
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        "Касовата бележка не съществува\nНомера на касовата бележка е задължителен\n"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_DELETE_ITEM"})
    public void deleteItemValidWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/item")
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(new ItemDeleteBindingModel(
                                receipt.getId().toString(),
                                2
                        )))
                )
                .andExpect(status().isOk());

        List<Item> items = itemService.getItems(receipt.getId()).stream()
                .sorted(Comparator.comparing(Item::getPosition))
                .toList();

        Assertions.assertEquals(2, items.size());

        Assertions.assertEquals(items.get(0).getPosition(), 1);
        Assertions.assertEquals(items.get(1).getPosition(), 2);
    }
}
