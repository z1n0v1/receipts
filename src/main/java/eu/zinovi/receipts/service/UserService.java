package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.binding.user.UserDetailsBindingModel;
import eu.zinovi.receipts.domain.model.mapper.UserToBindingDetails;
import eu.zinovi.receipts.domain.model.mapper.UserToDetails;
import eu.zinovi.receipts.domain.model.service.*;
import eu.zinovi.receipts.domain.model.entity.User;
import eu.zinovi.receipts.domain.model.view.UserDetailsView;
import eu.zinovi.receipts.domain.exception.AccessDeniedException;
import eu.zinovi.receipts.domain.exception.EntityNotFoundException;
import eu.zinovi.receipts.repository.RoleRepository;
import eu.zinovi.receipts.repository.UserRepository;
import eu.zinovi.receipts.domain.user.EmailUser;
import eu.zinovi.receipts.domain.user.GoogleOAuth2User;
import eu.zinovi.receipts.util.CloudStorage;
import eu.zinovi.receipts.util.ImageProcessing;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {
    private final UserToBindingDetails userToBindingDetails;
    private final UserToDetails userToDetails;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final EmailVerificationService emailVerificationService;
    private final VerificationTokenService verificationTokenService;
    private final CloudStorage cloudStorage;

    public UserService(UserToBindingDetails userToBindingDetails, UserToDetails userToDetails, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, EmailVerificationService emailVerificationService, VerificationTokenService verificationTokenService, CloudStorage cloudStorage) {
        this.userToBindingDetails = userToBindingDetails;
        this.userToDetails = userToDetails;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
        this.verificationTokenService = verificationTokenService;
        this.cloudStorage = cloudStorage;
    }

    public boolean checkCapability(String capability) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(capability));
    }

    public boolean checkPassword(String password) {

        return userRepository.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .map(u -> passwordEncoder.matches(password, u.getPassword()))
                .orElse(false);
    }

    public void changePassword(UserSettingsServiceModel userSettingsServiceModel) {
        User user = userRepository.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("Потребителят не е намерен"));

        user.setPassword(passwordEncoder.encode(userSettingsServiceModel.getPassword()));
        userRepository.saveAndFlush(user);

    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof EmailUser) {
            return userRepository.getByEmail(((EmailUser) principal).getEmail()).orElse(null);
        } else if (principal instanceof GoogleOAuth2User) {
            return userRepository.getByEmail(((GoogleOAuth2User) principal).getEmail()).orElse(null);
        } else {
            return null;
        }

//        OAuth2AuthenticationToken principal =  (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

//        principal.getUsername();
//        return userRepository.getByEmail(principal.).orElseThrow(() -> new IllegalArgumentException("User not found"));

//        return userRepository.getByEmail(principal.getEmail()).orElseThrow(() -> new IllegalArgumentException("User not found"));
//        return null;

    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean registerEmailUser(UserRegisterServiceModel userRegisterServiceModel) {
        if (userRepository.existsByEmail(userRegisterServiceModel.getEmail())) {
            return false;
        }
        User user = new User();
        user.setEmail(userRegisterServiceModel.getEmail());
        user.setFirstName(userRegisterServiceModel.getFirstName());
        user.setLastName(userRegisterServiceModel.getLastName());
//        user.setPicture(userRegisterServiceModel.getPicture());
        user.setEmailLoginDisabled(false);
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setPicture("/images/blank-avatar.png");
        user.setLastSeen(LocalDateTime.now());
        user.setRegisteredOn(LocalDateTime.now());
        user.setDisplayName(userRegisterServiceModel.getFirstName() + " " + userRegisterServiceModel.getLastName());
        user.getRoles().add(roleRepository.getByName("USER").orElse(null));
        user.setPassword(passwordEncoder.encode(userRegisterServiceModel.getPassword()));
        userRepository.saveAndFlush(user);
        emailVerificationService.sendVerificationEmail(user.getEmail());
        return true;
    }

    @Cacheable("emailVerification")
    public boolean emailNotVerified(String email) {
//        if (email == null) {
//            return false;
//        }
//        System.out.println("called emailNotVerified");
        return userRepository.existsByEmailAndEmailVerified(email, false);
    }

    @Transactional
    public void deleteUserByEmail(AdminUserDeleteServiceModel adminUserDeleteServiceModel) {
        User user = userRepository.getByEmail(adminUserDeleteServiceModel.getEmail())
                .orElseThrow(EntityNotFoundException::new);

        if (user.getRoles().contains(roleRepository.getByName("ADMIN").orElse(null))) {
            throw new AccessDeniedException("Нямате право да триете администратора");
        }

        verificationTokenService.deleteAllByUser(user);

        userRepository.deleteByEmail(
                adminUserDeleteServiceModel.getEmail());
    }

    public UserDetailsView getUserDetails() {
        return userToDetails.map(getCurrentUser());
    }

    public void savePicture(MultipartFile picture) throws IOException {

        String fileExtension = null;

        if (picture.getContentType() != null) {
            String[] contentType = picture.getContentType().split("/");
            if (contentType.length > 1) {
                fileExtension = contentType[1];
            }
        }

        if (fileExtension == null || picture.isEmpty() || picture.getSize() > 10000000 ||
                (!fileExtension.equals("jpeg")
                        && !fileExtension.equals("png")
                        && !fileExtension.equals("jpg"))) {
            throw new IllegalArgumentException("Неподдържан файлов формат.");
        }


        String pictureURL = cloudStorage.uploadFile(
//                picture.getInputStream(),
                ImageProcessing.compressAndScaleProfilePicture(picture.getInputStream()),
                "avatars", UUID.randomUUID() + ".jpg", true);

        User user = getCurrentUser();
        user.setPicture(pictureURL);
        userRepository.save(user);
    }

    public void setPassword(UserPasswordSetServiceModel userPasswordSetServiceModel) {
        User user = getCurrentUser();
        user.setPassword(passwordEncoder.encode(userPasswordSetServiceModel.getPassword()));
        user.setEmailLoginDisabled(false);
        userRepository.save(user);
    }

    public void editUser(UserDetailsServiceModel userDetailsServiceModel) {
        User user = getCurrentUser();
        user.setFirstName(userDetailsServiceModel.getFirstName());
        user.setLastName(userDetailsServiceModel.getLastName());
        user.setDisplayName(userDetailsServiceModel.getDisplayName());

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof EmailUser) {
            ((EmailUser) principal).setDisplayName(userDetailsServiceModel.getDisplayName());
        } else if (principal instanceof GoogleOAuth2User) {
        ((GoogleOAuth2User) principal).setDisplayName(userDetailsServiceModel.getDisplayName());
        }

        userRepository.save(user);
    }

    public UserDetailsBindingModel getCurrentUserBindingDetails() {
        User user = getCurrentUser();
        if (user == null) {
            return new UserDetailsBindingModel();
        }
        return userToBindingDetails.map(user);

    }

    public String getCurrentUserPicture() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof EmailUser) {
            return ((EmailUser) principal).getPicture();
        } else if (principal instanceof GoogleOAuth2User) {
            return ((GoogleOAuth2User) principal).getPicture();
        } else {
            return null;
        }
    }
}
