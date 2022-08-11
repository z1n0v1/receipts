package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.binding.user.UserDetailsBindingModel;
import eu.zinovi.receipts.domain.model.entity.User;
import eu.zinovi.receipts.domain.model.service.*;
import eu.zinovi.receipts.domain.model.view.UserDetailsView;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;

public interface UserService {

    boolean registerEmailUser(UserRegisterServiceModel userRegisterServiceModel);

    boolean emailExists(String email);

    boolean isEmailNotVerified(String email);

    boolean checkCapability(String capability);

    boolean checkPassword(String password);

    User getCurrentUser();

    String getCurrentUserPicture();

    UserDetailsView getCurrentUserDetails();

    UserDetailsBindingModel getCurrentUserBindingDetails();

    void editUser(UserDetailsServiceModel userDetailsServiceModel);

    void savePicture(MultipartFile picture) throws IOException;

    void setPassword(UserPasswordSetServiceModel userPasswordSetServiceModel);

    void changePassword(UserSettingsServiceModel userSettingsServiceModel);

    @Transactional
    void deleteUserByEmail(AdminUserDeleteServiceModel adminUserDeleteServiceModel);
}
