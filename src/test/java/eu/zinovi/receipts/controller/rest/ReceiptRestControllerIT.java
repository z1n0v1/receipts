package eu.zinovi.receipts.controller.rest;

import com.google.gson.Gson;
import eu.zinovi.receipts.WithMockEmailUser;
import eu.zinovi.receipts.domain.model.binding.receipt.ReceiptDeleteBindingModel;
import eu.zinovi.receipts.domain.model.binding.receipt.ReceiptEditBindingModel;
import eu.zinovi.receipts.domain.model.datatable.DatatableColumn;
import eu.zinovi.receipts.domain.model.datatable.DatatableOrder;
import eu.zinovi.receipts.domain.model.datatable.DatatableSearch;
import eu.zinovi.receipts.domain.model.datatable.FromDatatable;
import eu.zinovi.receipts.domain.model.entity.*;
import eu.zinovi.receipts.repository.*;
import eu.zinovi.receipts.service.ItemService;
import eu.zinovi.receipts.service.UserService;
import eu.zinovi.receipts.util.CloudStorage;
import eu.zinovi.receipts.util.ReceiptProcessApi;
import eu.zinovi.receipts.util.RegisterBGApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class ReceiptRestControllerIT {

    @MockBean
    ReceiptProcessApi receiptProcessApi;

    @MockBean
    CloudStorage cloudStorage;

    @MockBean
    RegisterBGApi registerBGApi;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

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
    private Company company;
    private Store store;

    @BeforeEach
    void setUp() {
        Address address = new Address("address");
        addressRepository.save(address);
        company = new Company("123456789", "company", "activity", address);
        companyRepository.save(company);
        Company company2 = new Company("987654321", "company2", "activity", address);
        companyRepository.save(company2);
        store = new Store("store", address, company);
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
        receiptRepository.save(receipt);
    }

    @Test
    @WithMockEmailUser
    public void uploadReceiptWithoutCap() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/receipts/sample-receipt.jpg");
        byte[] receiptBytes = is.readAllBytes();
        is.close();

        MockMultipartFile file = new MockMultipartFile("file",
                "sample-receipt.jpg", "image/jpg", receiptBytes);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/receipt")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        "Нямате право да добавяте касови бележки"));
    }

    // TODO: fix the file upload tests,
    //  should we mock receiptProcessApi ?
    /*
    @Test
    @WithMockEmailUser(roles = ("CAP_ADD_RECEIPT"))
    public void uploadReceiptWithCap() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/receipts/sample-receipt.jpg");
        byte[] receiptBytes = is.readAllBytes();
        is.close();

        MockMultipartFile file = new MockMultipartFile("file",
                "sample-receipt.jpg", "image/jpg", receiptBytes);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/receipt")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isOk());
    }
     */

    @Test
    @WithMockEmailUser
    public void getReceiptsWithoutCap() throws Exception {

        FromDatatable fromDatatable = new FromDatatable(
                1L,
                0,
                10,
                null,
                null,
                null,
                new DatatableColumn[]{
                        new DatatableColumn("0", "receptId", "false", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("1", "dateOfPurchase", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("2", "total", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("3", "categories", "true", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("4", "companyName", "true", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("5", "storeName", "true", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("6", "delete", "false", "false",
                                new DatatableSearch("", "false"))
                },
                new DatatableOrder[]{
                        new DatatableOrder("1", "desc")
                },
                new DatatableSearch("", "false")
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/receipt/list")
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(fromDatatable)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        "Нямате право да разглеждате касови бележки"));
    }

    @Test
    @WithMockEmailUser(roles = ("CAP_LIST_RECEIPTS"))
    public void getReceiptsInvalidWithCap() throws Exception {

        FromDatatable fromDatatable = new FromDatatable(
                1L,
                -1,
                10,
                null,
                null,
                null,
                new DatatableColumn[]{
                        new DatatableColumn("0", "receptId", "false", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("1", "dateOfPurchase", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("2", "total", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("3", "categories", "true", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("4", "companyName", "true", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("5", "storeName", "true", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("6", "delete", "false", "false",
                                new DatatableSearch("", "false"))
                },
                new DatatableOrder[]{
                        new DatatableOrder("1", "desc")
                },
                new DatatableSearch("", "false")
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/receipt/list")
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(fromDatatable)))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        "Началото на списъка трябва да е положително число\n"));
    }

    @Test
    @WithMockEmailUser(roles = ("CAP_LIST_RECEIPTS"))
    public void getReceiptsValidWithCapListReceipts() throws Exception {

        FromDatatable fromDatatable = new FromDatatable(
                1L,
                0,
                10,
                null,
                null,
                null,
                new DatatableColumn[]{
                        new DatatableColumn("0", "receptId", "false", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("1", "dateOfPurchase", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("2", "total", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("3", "categories", "true", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("4", "companyName", "true", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("5", "storeName", "true", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("6", "delete", "false", "false",
                                new DatatableSearch("", "false"))
                },
                new DatatableOrder[]{
                        new DatatableOrder("1", "desc")
                },
                new DatatableSearch("", "false")
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/receipt/list")
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(fromDatatable)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser(roles = ("CAP_LIST_ALL_RECEIPTS"))
    public void getReceiptsValidWithCapListAllReceipts() throws Exception {

        FromDatatable fromDatatable = new FromDatatable(
                1L,
                0,
                10,
                null,
                null,
                null,
                new DatatableColumn[]{
                        new DatatableColumn("0", "receptId", "false", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("1", "dateOfPurchase", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("2", "total", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("3", "categories", "true", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("4", "companyName", "true", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("5", "storeName", "true", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("6", "delete", "false", "false",
                                new DatatableSearch("", "false"))
                },
                new DatatableOrder[]{
                        new DatatableOrder("1", "desc")
                },
                new DatatableSearch("", "false")
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/receipt/list")
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(fromDatatable)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void saveReceiptWithoutCap() throws Exception {
        ReceiptEditBindingModel receiptEditBindingModel = new ReceiptEditBindingModel(
                receipt.getId().toString(),
                company.getEik(),
                store.getName(),
                receipt.getTotal(),
                store.getAddress().getValue(),
                receipt.getDateOfPurchase()
                );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/receipt")
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(receiptEditBindingModel)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        "Нямате право да редактирате касови бележки"));
    }

    @Test
    @WithMockEmailUser(roles = ("CAP_EDIT_RECEIPT"))
    public void saveReceiptInvalidWithCap() throws Exception {
        ReceiptEditBindingModel receiptEditBindingModel = new ReceiptEditBindingModel(
                receipt.getId().toString(),
                "1234",
                store.getName(),
                receipt.getTotal(),
                store.getAddress().getValue(),
                receipt.getDateOfPurchase()
                );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/receipt")
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(receiptEditBindingModel)))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        "ЕИК трябва да е съставен от 9 цифри\n"));
    }

    @Test
    @WithMockEmailUser(roles = ("CAP_EDIT_RECEIPT"))
    public void saveReceiptValidWithCapEditReceipts() throws Exception {
        ReceiptEditBindingModel receiptEditBindingModel = new ReceiptEditBindingModel(
                receipt.getId().toString(),
                "987654321", // company2 from SetUp
                store.getName(),
                receipt.getTotal(),
                store.getAddress().getValue(),
                receipt.getDateOfPurchase()
                );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/receipt")
                        .with(csrf())
                        .contentType("application/json")
                        .content(gson.toJson(receiptEditBindingModel)))
                .andExpect(status().isOk());

        Assertions.assertEquals("987654321", receiptRepository.
                findById(receipt.getId()).orElseThrow().getCompany().getEik());
    }

    @Test
    @WithMockEmailUser
    public void deleteReceiptWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/receipt")
                        .contentType("application/json")
                        .content(gson.toJson(new ReceiptDeleteBindingModel(
                                receipt.getId().toString())))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        "Нямате право да изтривате касови бележки"));
    }

    @Test
    @WithMockEmailUser(roles = ("CAP_DELETE_RECEIPT"))
    public void deleteReceiptInvalidWithCapDeleteReceipts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/receipt")
                        .contentType("application/json")
                        .content(gson.toJson(new ReceiptDeleteBindingModel(
                                "1234")))
                        .with(csrf()))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        "Касовата бележка не съществува\n"));
    }


    @Test
    @WithMockEmailUser(roles = ("CAP_DELETE_RECEIPT"))
    public void deleteReceiptValidWithCapDeleteReceipts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/receipt")
                        .contentType("application/json")
                        .content(gson.toJson(new ReceiptDeleteBindingModel(
                                receipt.getId().toString())))
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
