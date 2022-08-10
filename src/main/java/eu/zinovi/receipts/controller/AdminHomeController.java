package eu.zinovi.receipts.controller;

import eu.zinovi.receipts.service.impl.AddressServiceImpl;
import eu.zinovi.receipts.service.impl.StatisticsServiceImpl;
import eu.zinovi.receipts.service.impl.UserServiceImpl;
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

    private final AddressServiceImpl addressServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final StatisticsServiceImpl statisticsServiceImpl;

    @Value("${receipts.google.storage.bucket}")
    private String bucket;

    public AdminHomeController(AddressServiceImpl addressServiceImpl, UserServiceImpl userServiceImpl, StatisticsServiceImpl statisticsServiceImpl) {
        this.addressServiceImpl = addressServiceImpl;
        this.userServiceImpl = userServiceImpl;
        this.statisticsServiceImpl = statisticsServiceImpl;
    }

    @GetMapping("/")
    public String admin(Model model) {

        if (!userServiceImpl.checkCapability("CAP_ADMIN")) {
            return "redirect:/home";
        }

        model.addAttribute("statistics", statisticsServiceImpl.getStatistics());

        return "admin/home";
    }

    @GetMapping("/roles")
    public String roles() {
        if (!userServiceImpl.checkCapability("CAP_ADMIN") || !userServiceImpl.checkCapability("CAP_LIST_ROLES")) {
            return "redirect:/home";
        }

        return "admin/roles";
    }


    @GetMapping("/users")
    public String users() {
        if (!userServiceImpl.checkCapability("CAP_ADMIN") || !userServiceImpl.checkCapability("CAP_LIST_USERS")) {
            return "redirect:/home";
        }

        return "admin/users";
    }

    @GetMapping("/categories")
    public String categories() {
        if (!userServiceImpl.checkCapability("CAP_ADMIN") || !userServiceImpl.checkCapability("CAP_LIST_CATEGORIES")) {
            return "redirect:/home";
        }

        return "admin/categories";
    }

    @GetMapping("/receipt/{receiptId}")
    public String viewReceipt(@PathVariable("receiptId") UUID receiptId, Model model) {
        if (!userServiceImpl.checkCapability("CAP_ADMIN") || !userServiceImpl.checkCapability("CAP_ADMIN_VIEW_RECEIPT")) {
            return "redirect:/home";
        }

        model.addAttribute("receipt", addressServiceImpl.getReceipt(receiptId));
        model.addAttribute("bucket", bucket);

        return "admin/receipt-view";
    }
}
