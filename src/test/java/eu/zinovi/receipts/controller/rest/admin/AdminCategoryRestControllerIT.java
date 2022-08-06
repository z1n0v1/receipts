package eu.zinovi.receipts.controller.rest.admin;

import com.google.gson.Gson;
import eu.zinovi.receipts.WithMockEmailUser;
import eu.zinovi.receipts.domain.model.binding.admin.AdminCategoryAddBindingModel;
import eu.zinovi.receipts.domain.model.binding.admin.AdminCategoryDeleteBindingModel;
import eu.zinovi.receipts.domain.model.binding.admin.AdminCategorySaveBindingModel;
import eu.zinovi.receipts.domain.model.entity.Category;
import eu.zinovi.receipts.repository.CategoryRepository;
import eu.zinovi.receipts.util.CloudStorage;
import eu.zinovi.receipts.util.ReceiptProcessApi;
import eu.zinovi.receipts.util.RegisterBGApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
public class AdminCategoryRestControllerIT {

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
    private CategoryRepository categoryRepository;

    private Category category;


    @BeforeEach
    public void setUp() {
        category = new Category("Name", "#AAAAAA");
        categoryRepository.save(category);
    }

    @Test
    @WithMockEmailUser
    public void addCategoryWithoutCapability() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/category")
                                .content(gson.toJson(new AdminCategoryAddBindingModel("Нова категория", "FFFFFF")))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(NO_PERMISSION_CATEGORY_ADD));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_ADD_CATEGORY"})
    public void addCategoryInvalidWithCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/category")
                                .content(gson.toJson(new AdminCategoryAddBindingModel("Нова категория", "FFFFFF")))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(INVALID_CATEGORY_COLOR + "\n"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_ADD_CATEGORY"})
    public void addCategoryValidWithCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/category")
                                .content(gson.toJson(new AdminCategoryAddBindingModel("Нова категория", "#FFFFFF")))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void allCategoriesWithoutCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/admin/category/all")
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value(NO_PERMISSION_CATEGORY_LIST));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_LIST_CATEGORIES"})
    public void allCategoriesWithCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/admin/category/all")
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void updateCategoryWithoutCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/admin/category")
                                .content(gson.toJson(new AdminCategorySaveBindingModel(
                                        category.getId().toString(),
                                        "updatedName",
                                        "#BBBBBB")))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        NO_PERMISSION_CATEGORY_EDIT));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_EDIT_CATEGORY"})
    public void updateCategoryInvalidWithCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/admin/category")
                                .content(gson.toJson(new AdminCategorySaveBindingModel(
                                        category.getId().toString(),
                                        "updatedName",
                                        "#CCC")))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        INVALID_CATEGORY_COLOR + "\n"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_EDIT_CATEGORY"})
    public void updateCategoryValidWithCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/admin/category")
                                .content(gson.toJson(new AdminCategorySaveBindingModel(
                                        category.getId().toString(),
                                        "updatedName",
                                        "#BBBBBB")))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isOk());

        Category updatedCategory = categoryRepository.findById(category.getId())
                .orElseThrow(AssertionError::new);

            Assertions.assertEquals("updatedName", updatedCategory.getName());
            Assertions.assertEquals("#BBBBBB", updatedCategory.getColor());
    }

    @Test
    @WithMockEmailUser
    public void deleteCategoryWithoutCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/admin/category")
                                .content(gson.toJson(new AdminCategoryDeleteBindingModel(category.getId().toString())))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        NO_PERMISSION_CATEGORY_DELETE));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_DELETE_CATEGORY"})
    public void deleteCategoryInvalidWithCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/admin/category")
                                .content(gson.toJson(new AdminCategoryDeleteBindingModel("invalidId")))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(
                        INVALID_CATEGORY_ID + "\n"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_ADMIN", "CAP_DELETE_CATEGORY"})
    public void deleteCategoryValidWithCap() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/admin/category")
                                .content(gson.toJson(new AdminCategoryDeleteBindingModel(category.getId().toString())))
                                .contentType("application/json")
                                .with(csrf()))
                .andExpect(status().isOk());

        Assertions.assertFalse(categoryRepository.findById(category.getId()).isPresent());
    }
}
