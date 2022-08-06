package eu.zinovi.receipts.controller.rest;

import eu.zinovi.receipts.WithMockEmailUser;
import eu.zinovi.receipts.util.CloudStorage;
import eu.zinovi.receipts.util.ReceiptProcessApi;
import eu.zinovi.receipts.util.RegisterBGApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;

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
