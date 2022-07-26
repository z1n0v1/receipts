package eu.zinovi.receipts.controller.rest;

import com.google.gson.Gson;
import eu.zinovi.receipts.WithMockEmailUser;
import eu.zinovi.receipts.domain.model.binding.receipt.ReceiptEikBindingModel;
import eu.zinovi.receipts.domain.model.entity.Address;
import eu.zinovi.receipts.domain.model.entity.Company;
import eu.zinovi.receipts.repository.AddressRepository;
import eu.zinovi.receipts.repository.CompanyRepository;
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

import static eu.zinovi.receipts.util.constants.MessageConstants.INVALID_EIK;
import static eu.zinovi.receipts.util.constants.MessageConstants.NO_PERMISSION_RECEIPT_EDIT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CompanyRestControllerIT {

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
    private CompanyRepository companyRepository;
    @Autowired
    private AddressRepository addressRepository;

    @BeforeEach
    public void setUp() {
        Address address = new Address("address");
        addressRepository.save(address);
        companyRepository.save(new Company("123456789", "company", "activity", address));
    }

    @Test
    @WithMockEmailUser
    public void getByEikWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/company/eik")
                        .contentType("application/json")
                        .content(gson.toJson(new ReceiptEikBindingModel("123456789")))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        NO_PERMISSION_RECEIPT_EDIT));
    }

    @Test
    @WithMockEmailUser(roles = "CAP_EDIT_RECEIPT")
    public void getByEikInvalidEik() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/company/eik")
                        .contentType("application/json")
                        .content(gson.toJson(new ReceiptEikBindingModel("12345678")))
                        .with(csrf()))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        INVALID_EIK + "\n"));
    }

    @Test
    @WithMockEmailUser(roles = "CAP_EDIT_RECEIPT")
    public void getByEikWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/company/eik")
                        .contentType("application/json")
                        .content(gson.toJson(new ReceiptEikBindingModel("123456789")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("company"))
                .andExpect(jsonPath("$.companyAddress").value("address"));
    }
}
