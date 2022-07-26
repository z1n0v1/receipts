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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerIT {

    @MockBean
    ReceiptProcessApi receiptProcessApi;

    @MockBean
    CloudStorage cloudStorage;

    @MockBean
    RegisterBGApi registerBGApi;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockEmailUser(roles = {"CAP_VIEW_HOME"})
    public void testIndex() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @WithAnonymousUser
    public void testIndexAnonymous() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_VIEW_HOME"})
    public void testHome() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/home"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void testHomeWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithAnonymousUser
    public void testHomeAnonymous() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/user/login"));
    }
}
