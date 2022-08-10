package eu.zinovi.receipts.controller.rest.admin;

import com.google.gson.Gson;
import eu.zinovi.receipts.WithMockEmailUser;
import eu.zinovi.receipts.domain.model.binding.admin.*;
import eu.zinovi.receipts.service.impl.RoleServiceImpl;
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
public class AdminRoleRestControllerIT {

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
    private RoleServiceImpl roleServiceImpl;

    @Test
    @WithMockEmailUser
    public void addRoleWithoutCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/role")
                                .content(gson.toJson(new AdminRoleAddBindingModel(
                                        "newRole", new HashSet<>(){{
                                            add(new AdminCapabilityAddBindingModel("CAP_ADMIN"));
                                            add(new AdminCapabilityAddBindingModel("CAP_ADD_ROLE"));
                                            add(new AdminCapabilityAddBindingModel("CAP_LIST_ROLES"));
                                }})))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(NO_PERMISSION_ROLE_ADD));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_ADD_ROLE"})
    public void addRoleInvalidWithCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/role")
                                .content(gson.toJson(new AdminRoleAddBindingModel(
                                        "newRole", new HashSet<>(){{
                                            add(new AdminCapabilityAddBindingModel("INVALID_CAP"));
                                            add(new AdminCapabilityAddBindingModel("CAP_ADD_ROLE"));
                                            add(new AdminCapabilityAddBindingModel("CAP_LIST_ROLES"));
                                }})))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        INVALID_CAPABILITY + "\n"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_ADD_ROLE"})
    public void addRoleValidWithCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/role")
                                .content(gson.toJson(new AdminRoleAddBindingModel(
                                        "newRole", new HashSet<>(){{
                                            add(new AdminCapabilityAddBindingModel("CAP_ADMIN"));
                                            add(new AdminCapabilityAddBindingModel("CAP_ADD_ROLE"));
                                            add(new AdminCapabilityAddBindingModel("CAP_LIST_ROLES"));
                                }})))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isOk());

        Assertions.assertTrue(roleServiceImpl.existsByName("newRole"));
    }

    @Test
    @WithMockEmailUser
    public void listRolesWithoutCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/admin/role/all")
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        NO_PERMISSION_ROLE_LIST));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_LIST_ROLES"})
    public void listRolesValidWithCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/admin/role/all")
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void saveRoleWithoutCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/admin/role")
                                .content(gson.toJson(new AdminRoleBindingModel(
                                        "DEMO", new HashSet<>(){{
                                            add(new AdminCapabilityBindingModel("CAP_ADMIN", true));
                                            add(new AdminCapabilityBindingModel("CAP_ADD_ROLE", true));
                                            add(new AdminCapabilityBindingModel("CAP_LIST_ROLES", true));
                                }})))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(NO_PERMISSION_ROLE_EDIT));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_EDIT_ROLE"})
    public void saveRoleInvalidWithCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/admin/role")
                                .content(gson.toJson(new AdminRoleBindingModel(
                                        "DEMO", new HashSet<>(){{
                                            add(new AdminCapabilityBindingModel("INVALID_CAP", true));
                                            add(new AdminCapabilityBindingModel("CAP_EDIT_ROLE", true));
                                            add(new AdminCapabilityBindingModel("CAP_LIST_ROLES", true));
                                }})))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        INVALID_CAPABILITY + "\n"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_EDIT_ROLE"})
    public void saveRoleValidWithCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/admin/role")
                                .content(gson.toJson(new AdminRoleBindingModel(
                                        "DEMO", new HashSet<>(){{
                                            add(new AdminCapabilityBindingModel("CAP_ADMIN", true));
                                            add(new AdminCapabilityBindingModel("CAP_EDIT_ROLE", true));
                                            add(new AdminCapabilityBindingModel("CAP_LIST_ROLES", true));
                                }})))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void deleteRoleWithoutCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/admin/role")
                                .contentType("application/json")
                                .content(gson.toJson(new AdminRoleDeleteBindingModel("DEMO")))
                                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        NO_PERMISSION_ROLE_DELETE));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_DELETE_ROLE"})
    public void deleteRoleInvalidWithCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/admin/role")
                                .contentType("application/json")
                                .content(gson.toJson(new AdminRoleDeleteBindingModel("INVALID_ROLE")))
                                .with(csrf()))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        INVALID_ROLE + "\n"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_DELETE_ROLE"})
    public void deleteRoleValidWithUsersWithCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/admin/role")
                                .contentType("application/json")
                                .content(gson.toJson(new AdminRoleDeleteBindingModel("DEMO")))
                                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        NO_PERMISSION_ROLE_DELETE_WITH_ACTIVE_USERS));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_DELETE_ROLE", "CAP_ADD_ROLE" })
    public void createThenDeleteRoleValidWithCap() throws Exception {

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/role")
                                .content(gson.toJson(new AdminRoleAddBindingModel(
                                        "newRole", new HashSet<>(){{
                                    add(new AdminCapabilityAddBindingModel("CAP_ADMIN"));
                                    add(new AdminCapabilityAddBindingModel("CAP_ADD_ROLE"));
                                    add(new AdminCapabilityAddBindingModel("CAP_LIST_ROLES"));
                                }})))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isOk());

        Assertions.assertTrue(roleServiceImpl.existsByName("newRole"));

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/admin/role")
                                .contentType("application/json")
                                .content(gson.toJson(new AdminRoleDeleteBindingModel("newRole")))
                                .with(csrf()))
                .andExpect(status().isOk());

        Assertions.assertFalse(roleServiceImpl.existsByName("newRole"));
    }
}
