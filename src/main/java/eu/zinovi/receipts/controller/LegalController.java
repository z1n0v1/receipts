package eu.zinovi.receipts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {

    @GetMapping("/legal/terms-and-conditions")
    public String termsAndConditions() {
        return "legal/terms-and-conditions";
    }

    @GetMapping("/legal/privacy-policy")
    public String privacyPolicy() {
        return "legal/privacy-policy";
    }
}
