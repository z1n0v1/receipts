package eu.zinovi.receipts.controller.rest;

import eu.zinovi.receipts.WithMockEmailUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class HomeRestControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockEmailUser
    public void monthlyExpensesPieChartWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/home/expenses/categories/last-month/pie"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        "Нямате право да разглеждате месечните разходи по категории"));
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
                        "Нямате право да разглеждате разходите по категории"));
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
                        "Нямате право да разглеждате седмичните разходи през последния месец"));
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
                        "Нямате право да разглеждате статистиката"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_VIEW_HOME"})
    public void statisticsWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/home/statistics"))
                .andExpect(status().isOk());
    }
}
