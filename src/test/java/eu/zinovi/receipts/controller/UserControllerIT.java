package eu.zinovi.receipts.controller;

import eu.zinovi.receipts.WithMockEmailUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/login"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegister() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/register"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterPostWithCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .param("email", "test@test")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/register"));
    }

    @Test
    public void testRegisterPostWithoutCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .param("email", "test@test"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_VIEW_USER_DETAILS"})
    public void testUserDetails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/details"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void testUserDetailsWithoutCapability() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/details"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }


    @Test
    @WithMockEmailUser(roles = {"CAP_EDIT_USER"})
    public void testUserDetailsEdit() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/details/edit"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void testUserDetailsEditWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/details/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/details"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_EDIT_USER"})
    public void testUserDetailsEditPostWithCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/details/edit")
                        .param("firstName", "firstName")
                        .param("lastName", "lastName")
                        .param("displayName", "displayName")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/details"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_EDIT_USER"})
    public void testUserDetailsEditPostWithoutCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/details/edit")
                        .param("firstName", "firstName")
                        .param("lastName", "lastName")
                        .param("displayName", "displayName"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_EDIT_USER"})
    public void testUserDetailsEditPostWithInvalidInput() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/details/edit")
                        .param("firstName", "firstName")
                        .param("lastName", "lastName")
                        .param("displayName", "d")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/details/edit"));
    }

    @Test
    @WithMockEmailUser
    public void testUserDetailsEditPostWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/details/edit")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/details"));
    }

    @Test
    @WithMockEmailUser
    public void testUserDetailsSavePictureWithoutCap() throws Exception {

        MockMultipartFile file = new MockMultipartFile("picture", "test.png", "image/png",
                "test".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/user/details/picture/save")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/details"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_EDIT_USER"})
    public void testUserDetailsSavePictureWithCap() throws Exception {

        // Example of testing with an image that contains a receipt
//        InputStream is = this.getClass().getResourceAsStream("/receipts/sample-receipt.jpg");
//        byte[] receiptBytes = is.readAllBytes();
//        is.close();

        MockMultipartFile file = new MockMultipartFile("picture", "test.png", "image/bmp",
                "test".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/user/details/picture/save")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/details/edit"));
    }

    @Test
    @WithMockEmailUser
    public void testUserDetailsPasswordChangeGetWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/details/password/change"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/details"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_EDIT_USER", "CAP_CHANGE_PASSWORD"})
    public void testUserDetailsPasswordChangeGetWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/details/password/change"))
                .andExpect(status().isOk());
    }

    //TODO test password change without csrf
    //TODO test password change without the needed capabilities
    //TODO test password change with valid input

    @Test
    @WithMockEmailUser(roles = {"CAP_EDIT_USER", "CAP_CHANGE_PASSWORD"})
    public void testUserDetailsInvalidPasswordChangePostWithCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/details/password/change")
                        .param("oldPassword", "password") // invalid password
                        .param("newPassword", "password")
                        .param("confirmPassword", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/details/password/change"));
    }

    @Test
    @WithMockEmailUser
    public void testUserDetailsPasswordSetGetWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/details/password/set"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/details"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_EDIT_USER", "CAP_CHANGE_PASSWORD"})
    public void testUserDetailsPasswordSetGetWithCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/details/password/set"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockEmailUser
    public void testUserDetailsPasswordSetPostWithoutCap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/details/password/set")
                        .param("password", "password")
                        .param("confirmPassword", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/details"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_EDIT_USER", "CAP_CHANGE_PASSWORD"})
    public void testUserDetailsValidPasswordSetPostWithCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/details/password/set")
                        .param("password", "password")
                        .param("confirmPassword", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/details"));
    }

    @Test
    @WithMockEmailUser(roles = {"CAP_EDIT_USER", "CAP_CHANGE_PASSWORD"})
    public void testUserDetailsInvalidPasswordSetPostWithCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/details/password/set")
                        .param("password", "password")
                        .param("confirmPassword", "invalid")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/details/password/set"));
    }

    //TODO test password set without csrf

    @Test
    @WithMockEmailUser
    public void testVerifyEmailGetWithVerifiedUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/verify/email"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    public void testVerifyEmailGet() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/verify/email"))
                .andExpect(status().isOk());
    }

    //TODO test verify email with valid token

}
