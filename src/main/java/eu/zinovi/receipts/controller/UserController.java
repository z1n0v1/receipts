package eu.zinovi.receipts.controller;

import eu.zinovi.receipts.domain.model.binding.user.UserDetailsBindingModel;
import eu.zinovi.receipts.domain.model.binding.user.UserPasswordChangeBindingModel;
import eu.zinovi.receipts.domain.model.binding.user.UserPasswordSetBindingModel;
import eu.zinovi.receipts.domain.model.binding.user.UserRegisterBindingModel;
import eu.zinovi.receipts.domain.model.mapper.UserDetailsBindingToService;
import eu.zinovi.receipts.domain.model.mapper.UserPasswordSetBindingToService;
import eu.zinovi.receipts.domain.model.mapper.UserRegisterBindingToService;
import eu.zinovi.receipts.domain.model.mapper.UserSettingsServiceModelMapper;
import eu.zinovi.receipts.domain.user.EmailUser;
import eu.zinovi.receipts.service.EmailVerificationService;
import eu.zinovi.receipts.service.UserService;
import eu.zinovi.receipts.service.VerificationTokenService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserRegisterBindingToService userRegisterBindingToService;
    private final UserPasswordSetBindingToService userPasswordSetBindingToService;
    private final UserDetailsBindingToService userDetailsBindingToService;
    private final EmailVerificationService emailVerificationService;
    private final VerificationTokenService verificationTokenService;
    private final UserService userService;
    private final UserSettingsServiceModelMapper userSettingsServiceModelMapper;

    public UserController(
            UserRegisterBindingToService userRegisterBindingToService,
            UserPasswordSetBindingToService userPasswordSetBindingToService,
            UserDetailsBindingToService userDetailsBindingToService,
            EmailVerificationService emailVerificationService,
            VerificationTokenService verificationTokenService,
            UserService userService,
            UserSettingsServiceModelMapper userSettingsServiceModelMapper) {
        this.userRegisterBindingToService = userRegisterBindingToService;
        this.userPasswordSetBindingToService = userPasswordSetBindingToService;
        this.userDetailsBindingToService = userDetailsBindingToService;
        this.emailVerificationService = emailVerificationService;
        this.verificationTokenService = verificationTokenService;
        this.userService = userService;
        this.userSettingsServiceModelMapper = userSettingsServiceModelMapper;
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerConfirm(@Valid
                                  UserRegisterBindingModel userRegisterBindingModel,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors() ||
                !userService.registerEmailUser(userRegisterBindingToService.map(userRegisterBindingModel))) {
            redirectAttributes.addFlashAttribute("userRegisterBindingModel", userRegisterBindingModel);
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.userRegisterBindingModel",
                    bindingResult);

            return "redirect:/user/register";
        }
        redirectAttributes.addFlashAttribute("verifyEmail", true);
        return "redirect:/user/login";
    }

    @ModelAttribute
    public UserRegisterBindingModel userRegisterBindingModel() {
        return new UserRegisterBindingModel();
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/details")
    public String details(Model model) {

        if (!userService.checkCapability("CAP_VIEW_USER_DETAILS")) {
            return "redirect:/home";
        }

        model.addAttribute("user", userService.getCurrentUserDetails());

        return "user/details/home";
    }

    @GetMapping("/details/edit")
    public String detailsEdit(Model model) {

        if (!userService.checkCapability("CAP_EDIT_USER")) {
            return "redirect:/user/details";
        }

        model.addAttribute("picture", userService.getCurrentUserPicture());

        return "user/details/edit";
    }


    @PostMapping("/details/edit")
    public String detailsEditConfirm(@Valid
                                         UserDetailsBindingModel userDetailsBindingModel,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {

        if (!userService.checkCapability("CAP_EDIT_USER")) {
            return "redirect:/user/details";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userDetailsBindingModel", userDetailsBindingModel);
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.userDetailsBindingModel",
                    bindingResult);

            return "redirect:/user/details/edit";
        }
        userService.editUser(userDetailsBindingToService.map(userDetailsBindingModel));

        return "redirect:/user/details";
    }

    @ModelAttribute
    public UserDetailsBindingModel userDetailsBindingModel() {
        return userService.getCurrentUserBindingDetails();
    }

    @PostMapping("/details/picture/save")
    public String savePicture(@RequestParam("picture") MultipartFile picture, RedirectAttributes redirectAttributes) {

        if (!userService.checkCapability("CAP_EDIT_USER")) {
            return "redirect:/user/details";
        }

        try {
            userService.savePicture(picture);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/details/edit";
        }

        return "redirect:/user/details";
    }

    @GetMapping("/details/password/change")
    public String userPasswordChange() {

        if (!userService.checkCapability("CAP_EDIT_USER") || !userService.checkCapability("CAP_CHANGE_PASSWORD")) {
            return "redirect:/user/details";
        }

        return "user/details/password-change";
    }

    @PostMapping("/details/password/change")
    public String changePassword(@Valid UserPasswordChangeBindingModel userPasswordChangeBindingModel, BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {

        if (!userService.checkCapability("CAP_EDIT_USER") || !userService.checkCapability("CAP_CHANGE_PASSWORD")) {
            return "redirect:/user/details";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userPasswordChangeBindingModel", userPasswordChangeBindingModel)
                    .addFlashAttribute(
                            "org.springframework.validation.BindingResult.userPasswordChangeBindingModel",
                            bindingResult);
            return "redirect:/user/details/password/change";
        }

        userService.changePassword(userSettingsServiceModelMapper.INSTANCE
                .userSettingsServiceModel(userPasswordChangeBindingModel));

        redirectAttributes.addFlashAttribute("passwordChanged", true);
        return "redirect:/user/details";
    }

    @ModelAttribute
    public UserPasswordChangeBindingModel userSettingsBindingModel() {
        return new UserPasswordChangeBindingModel();
    }

    @GetMapping("/details/password/set")
    public String settingsPasswordSet() {

        if (!userService.checkCapability("CAP_EDIT_USER") || !userService.checkCapability("CAP_CHANGE_PASSWORD")) {
            return "redirect:/user/details";
        }

        return "user/details/password-set";
    }

    @PostMapping("/details/password/set")
    public String setPassword(@Valid UserPasswordSetBindingModel userPasswordSetBindingModel, BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {

        if (!userService.checkCapability("CAP_EDIT_USER") || !userService.checkCapability("CAP_CHANGE_PASSWORD")) {
            return "redirect:/user/details";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userPasswordSetBindingModel", userPasswordSetBindingModel)
                    .addFlashAttribute(
                            "org.springframework.validation.BindingResult.userPasswordSetBindingModel",
                            bindingResult);
            return "redirect:/user/details/password/set";
        }

        userService.setPassword(userPasswordSetBindingToService.map(userPasswordSetBindingModel));

        redirectAttributes.addFlashAttribute("passwordSet", true);
        return "redirect:/user/details";
    }

    @ModelAttribute
    public UserPasswordSetBindingModel userPasswordSetBindingModel() {
        return new UserPasswordSetBindingModel();
    }

    @GetMapping("/verify/email")
    public String verifyEmail(@RequestParam(value = "code", required = false) String code,
                              @RequestParam(value = "again", required = false) Boolean again,
                              RedirectAttributes redirectAttributes) {

        // principal instanceof EmailUser doesn't work if we inject Principal in the controller
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof EmailUser && ((EmailUser) principal).isEmailVerified()) {
            return "redirect:/home";
        }

        if (principal instanceof EmailUser user && again != null && again) {
            emailVerificationService.sendVerificationEmail(user.getEmail());
            redirectAttributes.addFlashAttribute("emailVerificationSent", true);
            return "redirect:/user/verify/email";
        }

        if (verificationTokenService.verifyToken(code)) {
            return "redirect:/home";
        }

        return "user/verify-email";

    }
}
