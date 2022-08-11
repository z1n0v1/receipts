package eu.zinovi.receipts.controller.rest;

import eu.zinovi.receipts.WithMockEmailUser;
import eu.zinovi.receipts.domain.model.entity.Category;
import eu.zinovi.receipts.domain.model.entity.Item;
import eu.zinovi.receipts.domain.model.entity.Receipt;
import eu.zinovi.receipts.repository.ReceiptRepository;
import eu.zinovi.receipts.service.CategoryService;
import eu.zinovi.receipts.service.ItemService;
import eu.zinovi.receipts.service.UserService;
import eu.zinovi.receipts.util.CloudStorage;
import eu.zinovi.receipts.util.ReceiptProcessApi;
import eu.zinovi.receipts.util.RegisterBGApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class HomeRestControllerIT {

    @MockBean
    ReceiptProcessApi receiptProcessApi;

    @MockBean
    CloudStorage cloudStorage;

    @MockBean
    RegisterBGApi registerBGApi;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ReceiptRepository receiptRepository;

    @Autowired
    ItemService itemService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    UserService userService;

    @BeforeEach
    public void setUp() throws Exception {

        Category category = categoryService.findByName("Други").orElseThrow();
        Item item = new Item();
        item.setName("Други");
        item.setPrice(BigDecimal.valueOf(10));
        item.setCategory(category);
        item.setPosition(1);
        item.setQuantity(BigDecimal.ONE);
        itemService.save(item);

        Receipt receipt = new Receipt();
        receipt.setAnalyzed(true);
        receipt.setUser(userService.getCurrentUser());
        receipt.setDateOfPurchase(LocalDateTime.now());
        receipt.setReceiptLines("");
        receipt.setTotal(BigDecimal.valueOf(10));
        receipt.setItemsTotal(BigDecimal.valueOf(10));
        receipt.getItems().add(item);
        receiptRepository.save(receipt);
    }

    @Test
    @WithMockEmailUser
    public void monthlyExpensesPieChartWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/home/expenses/categories/last-month/pie"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        NO_PERMISSION_MONTHLY_STATS_BY_CATEGORIES));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_VIEW_HOME"})
    public void monthlyExpensesPieChartWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/home/expenses/categories/last-month/pie"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void totalExpensesPieChartWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/home/expenses/categories/total/pie"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        NO_PERMISSION_STATS_BY_CATEGORIES));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_VIEW_HOME"})
    public void totalExpensesPieChartWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/home/expenses/categories/total/pie"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void lastMonthExpensesByWeekLineChartWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/home/expenses/last-month/by-week/line"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        NO_PERMISSION_STATS_MONTH_BY_WEEK));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_VIEW_HOME"})
    public void lastMonthExpensesByWeekLineChartWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/home/expenses/last-month/by-week/line"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void statistics() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/home/statistics"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        NO_PERMISSION_STATS_VIEW));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_VIEW_HOME"})
    public void statisticsWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/home/statistics"))
                .andExpect(status().isOk());
    }
}
