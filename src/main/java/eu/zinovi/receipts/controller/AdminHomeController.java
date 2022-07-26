package eu.zinovi.receipts.controller;

import eu.zinovi.receipts.service.AddressService;
import eu.zinovi.receipts.service.StatisticsService;
import eu.zinovi.receipts.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {

    private final AddressService addressService;
    private final UserService userService;
    private final StatisticsService statisticsService;

    @Value("${receipts.google.storage.bucket}")
    private String bucket;

    public AdminHomeController(AddressService addressService, UserService userService, StatisticsService statisticsService) {
        this.addressService = addressService;
        this.userService = userService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/")
    public String admin(Model model) {

        if (!userService.checkCapability("CAP_ADMIN")) {
            return "redirect:/";
        }

        model.addAttribute("statistics", statisticsService.getStatistics());

        return "admin/home";
    }

    @GetMapping("/roles")
    public String roles() {
        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_LIST_ROLES")) {
            return "redirect:/";
        }

        return "admin/roles";
    }


    @GetMapping("/users")
    public String users() {
        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_LIST_USERS")) {
            return "redirect:/";
        }

        return "admin/users";
    }

    @GetMapping("/receipts/view/{receiptId}")
    public String viewReceipt(@PathVariable("receiptId") UUID receiptId, Model model) {
        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_ADMIN_VIEW_RECEIPT")) {
            return "redirect:/";
        }

        model.addAttribute("receipt", addressService.getReceipt(receiptId));
        model.addAttribute("bucket", bucket);

        return "admin/receipt-view";
    }

    @GetMapping("/categories")
    public String categories() {
        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_LIST_CATEGORIES")) {
            return "redirect:/";
        }

        return "admin/categories";
    }
}
