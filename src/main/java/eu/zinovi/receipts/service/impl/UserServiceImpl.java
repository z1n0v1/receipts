package eu.zinovi.receipts.service.impl;

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
import eu.zinovi.receipts.service.EmailVerificationService;
import eu.zinovi.receipts.service.UserService;
import eu.zinovi.receipts.service.VerificationTokenService;
import eu.zinovi.receipts.util.CloudStorage;
import eu.zinovi.receipts.util.ImageProcessing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
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
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserToBindingDetails userToBindingDetails;
    private final UserToDetails userToDetails;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final EmailVerificationService emailVerificationService;
    private final VerificationTokenService verificationTokenService;
    private final CloudStorage cloudStorage;
    private final CacheManager cacheManager;

    public UserServiceImpl(
            UserToBindingDetails userToBindingDetails,
            UserToDetails userToDetails,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailVerificationService emailVerificationService,
            VerificationTokenService verificationTokenService,
            CloudStorage cloudStorage, CacheManager cacheManager) {
        this.userToBindingDetails = userToBindingDetails;
        this.userToDetails = userToDetails;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
        this.verificationTokenService = verificationTokenService;
        this.cloudStorage = cloudStorage;
        this.cacheManager = cacheManager;
    }

    @Override
    public boolean registerEmailUser(UserRegisterServiceModel userRegisterServiceModel) {
        if (userRepository.existsByEmail(userRegisterServiceModel.getEmail())) {
            return false;
        }
        User user = new User();
        user.setEmail(userRegisterServiceModel.getEmail());
        user.setFirstName(userRegisterServiceModel.getFirstName());
        user.setLastName(userRegisterServiceModel.getLastName());
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
        try {
            cacheManager.getCache("userDetails").evict(user.getEmail());
        } catch (NullPointerException e) {
            LOGGER.error("Error evicting cache for email {}", user.getEmail());
        }
        return true;
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Cacheable("emailVerification")
    public boolean isEmailNotVerified(String email) {
        return userRepository.existsByEmailAndEmailVerified(email, false);
    }

    @Override
    public boolean checkCapability(String capability) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(capability));
    }

    @Override
    public boolean checkPassword(String password) {

        return userRepository.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .map(u -> passwordEncoder.matches(password, u.getPassword()))
                .orElse(false);
    }

    @Override
    @Cacheable("userDetails")
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof EmailUser) {
            return userRepository.getByEmail(((EmailUser) principal).getEmail()).orElse(null);
        } else if (principal instanceof GoogleOAuth2User) {
            return userRepository.getByEmail(((GoogleOAuth2User) principal).getEmail()).orElse(null);
        } else {
            return null;
        }
    }

    @Override
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

    @Override
    public UserDetailsView getCurrentUserDetails() {
        return userToDetails.map(getCurrentUser());
    }

    @Override
    public UserDetailsBindingModel getCurrentUserBindingDetails() {
        User user = getCurrentUser();
        if (user == null) {
            return new UserDetailsBindingModel();
        }
        return userToBindingDetails.map(user);

    }

    @Override
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
        try {
            cacheManager.getCache("userDetails").evict(user.getEmail());
        } catch (NullPointerException e) {
            LOGGER.error("Error evicting cache for email {}", user.getEmail());
        }
    }

    @Override
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
                ImageProcessing.compressAndScaleProfilePicture(picture.getInputStream()),
                "avatars", UUID.randomUUID() + ".jpg", true);

        User user = getCurrentUser();
        user.setPicture(pictureURL);
        userRepository.save(user);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof EmailUser) {
            ((EmailUser) principal).setPicture(pictureURL);
        } else if (principal instanceof GoogleOAuth2User) {
            ((GoogleOAuth2User) principal).setPicture(pictureURL);
        }
        try {
            cacheManager.getCache("userDetails").evict(user.getEmail());
        } catch (NullPointerException e) {
            LOGGER.error("Error evicting cache for email {}", user.getEmail());
        }
    }

    @Override
    public void setPassword(UserPasswordSetServiceModel userPasswordSetServiceModel) {
        User user = getCurrentUser();
        user.setPassword(passwordEncoder.encode(userPasswordSetServiceModel.getPassword()));
        user.setEmailLoginDisabled(false);
        userRepository.save(user);
        try {
            cacheManager.getCache("userDetails").evict(user.getEmail());
        } catch (NullPointerException e) {
            LOGGER.error("Error evicting cache for email {}", user.getEmail());
        }
    }

    @Override
    public void changePassword(UserSettingsServiceModel userSettingsServiceModel) {
        User user = userRepository.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("Потребителят не е намерен"));

        user.setPassword(passwordEncoder.encode(userSettingsServiceModel.getPassword()));
        userRepository.saveAndFlush(user);
        try {
            cacheManager.getCache("userDetails").evict(user.getEmail());
        } catch (NullPointerException e) {
            LOGGER.error("Error evicting cache for email {}", user.getEmail());
        }

    }

    @Override
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
        try {
            cacheManager.getCache("userDetails").evict(user.getEmail());
        } catch (NullPointerException e) {
            LOGGER.error("Error evicting cache for email {}", user.getEmail());
        }
    }
}
