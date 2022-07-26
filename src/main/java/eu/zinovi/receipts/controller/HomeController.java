package eu.zinovi.receipts.controller;

import eu.zinovi.receipts.service.StatisticsService;
import eu.zinovi.receipts.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final UserService userService;
    private final StatisticsService statisticsService;

    public HomeController(UserService userService, StatisticsService statisticsService) {
        this.userService = userService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/")
    public String index(Model model) {
        if (userService.checkCapability("CAP_VIEW_HOME")) {
            return "redirect:/home";
        }

        model.addAttribute("indexStatistics", statisticsService.getIndexStatistics());

        return "index";
    }

    @GetMapping("/home")
    public String home() {
        if (!userService.checkCapability("CAP_VIEW_HOME")) {
            return "redirect:/";
        }
        return "home";
    }
}
