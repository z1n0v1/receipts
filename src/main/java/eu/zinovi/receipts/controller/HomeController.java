package eu.zinovi.receipts.controller;

import eu.zinovi.receipts.service.impl.StatisticsServiceImpl;
import eu.zinovi.receipts.service.impl.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final UserServiceImpl userServiceImpl;
    private final StatisticsServiceImpl statisticsServiceImpl;

    public HomeController(UserServiceImpl userServiceImpl, StatisticsServiceImpl statisticsServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        this.statisticsServiceImpl = statisticsServiceImpl;
    }

    @GetMapping("/")
    public String index(Model model) {
        if (userServiceImpl.checkCapability("CAP_VIEW_HOME")) {
            return "redirect:/home";
        }

        model.addAttribute("indexStatistics", statisticsServiceImpl.getIndexStatistics());

        return "index";
    }

    @GetMapping("/home")
    public String home() {
        if (!userServiceImpl.checkCapability("CAP_VIEW_HOME")) {
            return "redirect:/";
        }
        return "home";
    }
}
