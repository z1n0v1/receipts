package eu.zinovi.receipts.controller;

import eu.zinovi.receipts.service.ReceiptsService;
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

    private final UserService userService;
    private final ReceiptsService receiptsService;
    private final StatisticsService statisticsService;

    @Value("${receipts.google.storage.bucket}")
    private String bucket;

    public AdminHomeController(
            UserService userService,
            ReceiptsService receiptsService,
            StatisticsService statisticsService) {
        this.userService = userService;
        this.receiptsService = receiptsService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/")
    public String admin(Model model) {

        if (!userService.checkCapability("CAP_ADMIN")) {
            return "redirect:/home";
        }

        model.addAttribute("statistics", statisticsService.getStatistics());

        return "admin/home";
    }

    @GetMapping("/roles")
    public String roles() {
        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_LIST_ROLES")) {
            return "redirect:/home";
        }

        return "admin/roles";
    }


    @GetMapping("/users")
    public String users() {
        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_LIST_USERS")) {
            return "redirect:/home";
        }

        return "admin/users";
    }

    @GetMapping("/categories")
    public String categories() {
        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_LIST_CATEGORIES")) {
            return "redirect:/home";
        }

        return "admin/categories";
    }

    @GetMapping("/receipt/{receiptId}")
    public String viewReceipt(@PathVariable("receiptId") UUID receiptId, Model model) {
        if (!userService.checkCapability("CAP_ADMIN") || !userService.checkCapability("CAP_ADMIN_VIEW_RECEIPT")) {
            return "redirect:/home";
        }

        model.addAttribute("receipt", receiptsService.getAdminReceipt(receiptId));
        model.addAttribute("bucket", bucket);

        return "admin/receipt-view";
    }
}
