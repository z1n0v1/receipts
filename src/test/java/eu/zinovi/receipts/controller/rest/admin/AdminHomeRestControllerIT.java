package eu.zinovi.receipts.controller.rest.admin;

import com.google.gson.Gson;
import eu.zinovi.receipts.WithMockEmailUser;
import eu.zinovi.receipts.domain.model.datatable.DatatableColumn;
import eu.zinovi.receipts.domain.model.datatable.DatatableOrder;
import eu.zinovi.receipts.domain.model.datatable.DatatableSearch;
import eu.zinovi.receipts.domain.model.datatable.FromDatatable;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class AdminHomeRestControllerIT {
    @MockBean
    ReceiptProcessApi receiptProcessApi;

    @MockBean
    CloudStorage cloudStorage;

    @MockBean
    RegisterBGApi registerBGApi;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    Gson gson;

    @Test
    @WithMockEmailUser
    public void receiptsListWithoutCap() throws Exception {

        FromDatatable fromDatatable = new FromDatatable(
                1L,
                0,
                10,
                null,
                null,
                null,
                new DatatableColumn[]{
                        new DatatableColumn("0", "receiptId", "true", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("1", "addedOn", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("2", "addedBy", "false", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("3", "isProcessed", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("4", "total", "false", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("5", "itemsTotal", "false", "false",
                                new DatatableSearch("", "false"))
                },
                new DatatableOrder[]{
                        new DatatableOrder("1", "desc")
                },
                new DatatableSearch("", "false")
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/receipt/all")
                                .contentType("application/json")
                                .content(gson.toJson(fromDatatable))
                                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        NO_PERMISSION_RECEIPT_LIST));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_ADMIN_LIST_RECEIPTS"})
    public void receiptListInvalidWithCap() throws Exception {
        FromDatatable fromDatatable = new FromDatatable(
                null,
                0,
                10,
                null,
                null,
                null,
                new DatatableColumn[]{
                        new DatatableColumn("0", "receiptId", "true", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("1", "addedOn", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("2", "addedBy", "false", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("3", "isProcessed", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("4", "total", "false", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("5", "itemsTotal", "false", "false",
                                new DatatableSearch("", "false"))
                },
                new DatatableOrder[]{
                        new DatatableOrder("1", "desc")
                },
                new DatatableSearch("", "false")
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/receipt/all")
                                .contentType("application/json")
                                .content(gson.toJson(fromDatatable))
                                .with(csrf()))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        MISSING_REQUEST_ID + "\n"));

    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_ADMIN_LIST_RECEIPTS"})
    public void receiptListValidWithCap() throws Exception {
        FromDatatable fromDatatable = new FromDatatable(
                1L,
                0,
                10,
                null,
                null,
                null,
                new DatatableColumn[]{
                        new DatatableColumn("0", "receiptId", "true", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("1", "addedOn", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("2", "addedBy", "false", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("3", "isProcessed", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("4", "total", "false", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("5", "itemsTotal", "false", "false",
                                new DatatableSearch("", "false"))
                },
                new DatatableOrder[]{
                        new DatatableOrder("1", "desc")
                },
                new DatatableSearch("", "false")
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/receipt/all")
                                .contentType("application/json")
                                .content(gson.toJson(fromDatatable))
                                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void getCapabilitiesWithoutCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/admin/capability/all")
                                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        NO_PERMISSION_CAPABILITY_LIST));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_LIST_CAPABILITIES"})
    public void getCapabilitiesWithCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/admin/capability/all")
                                .with(csrf()))
                .andExpect(status().isOk());
    }
}
