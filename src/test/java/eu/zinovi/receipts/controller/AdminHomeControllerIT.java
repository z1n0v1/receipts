package eu.zinovi.receipts.controller;

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

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminHomeControllerIT {

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
    public void testIndex() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN"})
    public void testIndexWithAdminRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void testRolesWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/roles"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_LIST_ROLES", "CAP_ADMIN"})
    public void testRolesWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/roles"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void testUsersWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_LIST_USERS", "CAP_ADMIN"})
    public void testUsersWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void testCategoriesWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/categories"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_LIST_CATEGORIES", "CAP_ADMIN"})
    public void testCategoriesWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/categories"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void testReceiptsWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/receipt/{id}", UUID.randomUUID())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN_VIEW_RECEIPT", "CAP_ADMIN"})
    public void testReceiptsWithInvalidIdWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/receipt/{id}", UUID.randomUUID())
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

}
