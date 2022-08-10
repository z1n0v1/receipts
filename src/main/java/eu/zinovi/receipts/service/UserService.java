package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.binding.user.UserDetailsBindingModel;
import eu.zinovi.receipts.domain.model.entity.User;
import eu.zinovi.receipts.domain.model.service.*;
import eu.zinovi.receipts.domain.model.view.UserDetailsView;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;

public interface UserService {
    boolean checkCapability(String capability);

    boolean checkPassword(String password);

    void changePassword(UserSettingsServiceModel userSettingsServiceModel);

    User getCurrentUser();

    boolean emailExists(String email);

    boolean registerEmailUser(UserRegisterServiceModel userRegisterServiceModel);

    boolean emailNotVerified(String email);

    @Transactional
    void deleteUserByEmail(AdminUserDeleteServiceModel adminUserDeleteServiceModel);

    UserDetailsView getUserDetails();

    void savePicture(MultipartFile picture) throws IOException;

    void setPassword(UserPasswordSetServiceModel userPasswordSetServiceModel);

    void editUser(UserDetailsServiceModel userDetailsServiceModel);

    UserDetailsBindingModel getCurrentUserBindingDetails();

    String getCurrentUserPicture();
}
