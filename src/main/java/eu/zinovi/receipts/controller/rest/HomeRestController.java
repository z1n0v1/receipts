package eu.zinovi.receipts.controller.rest;

import be.ceau.chart.LineChart;
import be.ceau.chart.PieChart;
import eu.zinovi.receipts.domain.model.view.HomeStatisticsView;
import eu.zinovi.receipts.domain.exception.AccessDeniedException;
import eu.zinovi.receipts.service.UserService;
import eu.zinovi.receipts.service.ExpensesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@RestController
@RequestMapping("/api/home")
public class HomeRestController {
    private final ExpensesService expensesService;
    private final UserService userService;

    public HomeRestController(ExpensesService expensesService, UserService userService) {
        this.expensesService = expensesService;
        this.userService = userService;
    }

    @RequestMapping(value = "/expenses/categories/last-month/pie", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<PieChart> monthlyExpensesPieChart() {

        if (!userService.checkCapability("CAP_VIEW_HOME")) {
            throw new AccessDeniedException(NO_PERMISSION_MONTHLY_STATS_BY_CATEGORIES);
        }

        return ResponseEntity.ok(expensesService.monthlyExpensesByCategoryPieChart());
    }

    @RequestMapping(value = "/expenses/categories/total/pie", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<PieChart> totalExpensesPieChart() {

        if (!userService.checkCapability("CAP_VIEW_HOME")) {
            throw new AccessDeniedException(NO_PERMISSION_STATS_BY_CATEGORIES);
        }

        return ResponseEntity.ok(expensesService.totalExpensesByCategoryPieChart());
    }

    @RequestMapping(value = "/expenses/last-month/by-week/line", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<LineChart> lastMonthExpensesByWeekLineChart() {

        if (!userService.checkCapability("CAP_VIEW_HOME")) {
            throw new AccessDeniedException(NO_PERMISSION_STATS_MONTH_BY_WEEK);
        }

        return ResponseEntity.ok(expensesService.lastMonthExpensesByWeekLineChart());
    }

    @RequestMapping(value = "/statistics", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<HomeStatisticsView> statistics() {

        if (!userService.checkCapability("CAP_VIEW_HOME")) {
            throw new AccessDeniedException(NO_PERMISSION_STATS_VIEW);
        }

        return ResponseEntity.ok(expensesService.statistics());
    }
}
