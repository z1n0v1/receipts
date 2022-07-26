package eu.zinovi.receipts.controller.rest.admin;

import com.google.gson.Gson;
import eu.zinovi.receipts.WithMockEmailUser;
import eu.zinovi.receipts.domain.model.binding.admin.AdminGetUserDetailsBindingModel;
import eu.zinovi.receipts.domain.model.binding.admin.AdminUserDeleteBindingModel;
import eu.zinovi.receipts.domain.model.binding.admin.AdminUserRoleBindingModel;
import eu.zinovi.receipts.domain.model.binding.admin.AdminUserSaveBindingModel;
import eu.zinovi.receipts.domain.model.datatable.DatatableColumn;
import eu.zinovi.receipts.domain.model.datatable.DatatableOrder;
import eu.zinovi.receipts.domain.model.datatable.DatatableSearch;
import eu.zinovi.receipts.domain.model.datatable.FromDatatable;
import eu.zinovi.receipts.service.impl.UserServiceImpl;
import eu.zinovi.receipts.util.CloudStorage;
import eu.zinovi.receipts.util.ReceiptProcessApi;
import eu.zinovi.receipts.util.RegisterBGApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;

import java.util.HashSet;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class AdminUserRestControllerIT {

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
    private UserServiceImpl userServiceImpl;

    @Test
    @WithMockEmailUser
    public void getUserDetailsWithoutCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/user")
                                .content(gson.toJson(new AdminGetUserDetailsBindingModel("demo@demo")))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        NO_PERMISSION_USER_DETAILS));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_LIST_USERS"})
    public void getUserDetailsInvalidWithCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/user")
                                .content(gson.toJson(new AdminGetUserDetailsBindingModel("invalidEmail")))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        INVALID_EMAIL + "\n" + INVALID_USER + "\n"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_LIST_USERS"})
    public void getUserDetailsValidWithCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/user")
                                .content(gson.toJson(new AdminGetUserDetailsBindingModel("demo@demo")))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void usersListWithoutCap() throws Exception {
        FromDatatable fromDatatable = new FromDatatable(
                0L,
                0,
                10,
                null,
                null,
                null,
                new DatatableColumn[]{
                        new DatatableColumn("0", "registeredOn", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("1", "lastSeen", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("2", "email", "true", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("3", "receipts", "false", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("4", "total", "false", "false",
                                new DatatableSearch("", "false")),
                },
                new DatatableOrder[]{
                        new DatatableOrder("1", "desc")
                },
                new DatatableSearch("", "false")
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/user/all")
                                .content(gson.toJson(fromDatatable))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        NO_PERMISSION_USER_LIST));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_LIST_USERS"})
    public void usersListInvalidWithCap() throws Exception {
        FromDatatable fromDatatable = new FromDatatable(
                0L,
                -1,
                10,
                null,
                null,
                null,
                new DatatableColumn[]{
                        new DatatableColumn("0", "registeredOn", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("1", "lastSeen", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("2", "email", "true", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("3", "receipts", "false", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("4", "total", "false", "false",
                                new DatatableSearch("", "false")),
                },
                new DatatableOrder[]{
                        new DatatableOrder("1", "desc")
                },
                new DatatableSearch("", "false")
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/user/all")
                                .content(gson.toJson(fromDatatable))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        NEGATIVE_LIST_START_INDEX + "\n"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_LIST_USERS"})
    public void usersListValidWithCap() throws Exception {
        FromDatatable fromDatatable = new FromDatatable(
                0L,
                0,
                10,
                null,
                null,
                null,
                new DatatableColumn[]{
                        new DatatableColumn("0", "registeredOn", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("1", "lastSeen", "false", "true",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("2", "email", "true", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("3", "receipts", "false", "false",
                                new DatatableSearch("", "false")),
                        new DatatableColumn("4", "total", "false", "false",
                                new DatatableSearch("", "false")),
                },
                new DatatableOrder[]{
                        new DatatableOrder("1", "desc")
                },
                new DatatableSearch("", "false")
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/user/all")
                                .content(gson.toJson(fromDatatable))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void saveUserDetailsWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/user")
                        .contentType("application/json")
                        .content(gson.toJson(new AdminUserSaveBindingModel(
                                "firstName",
                                "lastName",
                                "displayName",
                                "demo@demo",
                                true,
                                false,
                                new HashSet<>() {{
                                    add(new AdminUserRoleBindingModel("DEMO", true));
                                    add(new AdminUserRoleBindingModel("USER", false));
                                }})))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        NO_PERMISSION_USER_EDIT));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_EDIT_ALL_USERS"})
    public void saveUserDetailsInvalidWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/user")
                        .contentType("application/json")
                        .content(gson.toJson(new AdminUserSaveBindingModel(
                                "",
                                "lastName",
                                "displayName",
                                "demo@demo",
                                true,
                                false,
                                new HashSet<>() {{
                                    add(new AdminUserRoleBindingModel("DEMO", true));
                                    add(new AdminUserRoleBindingModel("USER", false));
                                }})))
                        .with(csrf()))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        REQUIRED_NAME + "\n"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_EDIT_ALL_USERS"})
    public void saveUserDetailsValidWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/user")
                        .contentType("application/json")
                        .content(gson.toJson(new AdminUserSaveBindingModel(
                                "firstName",
                                "lastName",
                                "displayName",
                                "demo@demo",
                                true,
                                false,
                                new HashSet<>() {{
                                    add(new AdminUserRoleBindingModel("DEMO", true));
                                    add(new AdminUserRoleBindingModel("USER", false));
                                }})))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void deleteUserDetailsWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/user")
                        .contentType("application/json")
                        .content(gson.toJson(new AdminUserDeleteBindingModel(
                                "demo@demo")))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        NO_PERMISSION_USER_DELETE));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_DELETE_USER"})
    public void deleteUserDetailsInvalidWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/user")
                        .contentType("application/json")
                        .content(gson.toJson(new AdminUserDeleteBindingModel(
                                "")))
                        .with(csrf()))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        REQUIRED_EMAIL +"\n" + INVALID_USER + "\n"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_DELETE_USER"})
    public void deleteUserDetailsValidWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/user")
                        .contentType("application/json")
                        .content(gson.toJson(new AdminUserDeleteBindingModel(
                                "demo@demo")))
                        .with(csrf()))
                .andExpect(status().isOk());

        Assertions.assertFalse(userServiceImpl.emailExists("demo@demo"));

    }
}
