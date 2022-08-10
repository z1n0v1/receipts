package eu.zinovi.receipts.controller;

import eu.zinovi.receipts.service.impl.ReceiptsServiceImpl;
import eu.zinovi.receipts.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/receipt")
public class ReceiptController {

    private final ReceiptsServiceImpl receiptService;
    private final UserServiceImpl userServiceImpl;

    @Value("${receipts.google.maps.api-key}")
    private String googleMapsApiKey;

    public ReceiptController(ReceiptsServiceImpl receiptService, UserServiceImpl userServiceImpl) {
        this.receiptService = receiptService;
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping("/add")
    public String addReceipt() {

        if (!userServiceImpl.checkCapability("CAP_ADD_RECEIPT")) {
            return "redirect:/receipt/all";
        }

        return "receipt/add";
    }



    @GetMapping("/all")
    public String listReceipts(Model model) {

        if (!userServiceImpl.checkCapability("CAP_LIST_RECEIPTS") &&
                !userServiceImpl.checkCapability("CAP_LIST_ALL_RECEIPTS")) {
            return "redirect:/home";
        }

        if (userServiceImpl.checkCapability("CAP_LIST_ALL_RECEIPTS")) {
            model.addAttribute("receipts", receiptService.getAllReceiptImagesWithDate());
        } else {
            model.addAttribute("receipts", receiptService.getReceiptImagesWithDate());
        }

        return "receipt/all";
    }

    @GetMapping("/details/{id}")
    public String getReceiptDetails(@PathVariable("id") UUID id, Model model) {
        if (!userServiceImpl.checkCapability("CAP_VIEW_RECEIPT")) {
            return "redirect:/receipt/all";
        }
        model.addAttribute("receipt", receiptService.getReceiptDetails(id));
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);

        return "receipt/details";
    }
}
