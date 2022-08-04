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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReceiptControllerIT {

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
    public void testAddReceiptWithoutCapability() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipt/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/receipt/all"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADD_RECEIPT"})
    public void testAddReceiptWithCapability() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipt/add"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void testListReceiptsWithoutCapability() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipt/all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_LIST_RECEIPTS"})
    public void testListReceiptsWithCapabilityListReceipts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipt/all"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_LIST_ALL_RECEIPTS"})
    public void testListReceiptsWithCapabilityListAllReceipts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipt/all"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void testGetReceiptDetailsWithoutCapability() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipt/details/{id}", UUID.randomUUID()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/receipt/all"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_VIEW_RECEIPT"})
    public void testGetReceiptDetailsWithCapabilityWithInvalidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipt/details/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    // TODO: test getReceiptDetails with capability and valid id
}
