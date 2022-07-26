package eu.zinovi.receipts.controller;

import eu.zinovi.receipts.service.ReceiptsService;
import eu.zinovi.receipts.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/receipts")
public class ReceiptController {

    private final ReceiptsService receiptService;
    private final UserService userService;

    @Value("${receipts.google.maps.api-key}")
    private String googleMapsApiKey;

    public ReceiptController(ReceiptsService receiptService, UserService userService) {
        this.receiptService = receiptService;
        this.userService = userService;
    }

    @GetMapping("/add")
    public String addReceipt() {

        if (!userService.checkCapability("CAP_ADD_RECEIPT")) {
            return "redirect:/receipt/all";
        }

        return "receipt/add";
    }



    @GetMapping("/all")
    public String listReceipts(Model model) {

        if (!userService.checkCapability("CAP_LIST_RECEIPTS") &&
                !userService.checkCapability("CAP_LIST_ALL_RECEIPTS")) {
            return "redirect:/home";
        }

        if (userService.checkCapability("CAP_LIST_ALL_RECEIPTS")) {
            model.addAttribute("receipts", receiptService.getAllReceiptImagesWithDate());
        } else {
            model.addAttribute("receipts", receiptService.getReceiptImagesWithDate());
        }

        return "receipt/all";
    }

    @GetMapping("/details/{id}")
    public String getReceiptDetails(@PathVariable("id") UUID id, Model model) {
        if (!userService.checkCapability("CAP_VIEW_RECEIPT")) {
            return "redirect:/receipt/all";
        }
        model.addAttribute("receipt", receiptService.getReceiptDetails(id));
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);

        return "receipt/details";
    }
}
