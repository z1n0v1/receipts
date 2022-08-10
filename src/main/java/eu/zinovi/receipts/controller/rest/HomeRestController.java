package eu.zinovi.receipts.controller.rest;

import be.ceau.chart.LineChart;
import be.ceau.chart.PieChart;
import eu.zinovi.receipts.domain.model.view.HomeStatisticsView;
import eu.zinovi.receipts.domain.exception.AccessDeniedException;
import eu.zinovi.receipts.service.impl.ExpensesServiceImpl;
import eu.zinovi.receipts.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@RestController
@RequestMapping("/api/home")
public class HomeRestController {
    private final ExpensesServiceImpl expensesServiceImpl;
    private final UserServiceImpl userServiceImpl;

    public HomeRestController(ExpensesServiceImpl expensesServiceImpl, UserServiceImpl userServiceImpl) {
        this.expensesServiceImpl = expensesServiceImpl;
        this.userServiceImpl = userServiceImpl;
    }

    @RequestMapping(value = "/expenses/categories/last-month/pie", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<PieChart> monthlyExpensesPieChart() {

        if (!userServiceImpl.checkCapability("CAP_VIEW_HOME")) {
            throw new AccessDeniedException(NO_PERMISSION_MONTHLY_STATS_BY_CATEGORIES);
        }

        return ResponseEntity.ok(expensesServiceImpl.monthlyExpensesByCategoryPieChart());
    }

    @RequestMapping(value = "/expenses/categories/total/pie", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<PieChart> totalExpensesPieChart() {

        if (!userServiceImpl.checkCapability("CAP_VIEW_HOME")) {
            throw new AccessDeniedException(NO_PERMISSION_STATS_BY_CATEGORIES);
        }

        return ResponseEntity.ok(expensesServiceImpl.totalExpensesByCategoryPieChart());
    }

    @RequestMapping(value = "/expenses/last-month/by-week/line", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<LineChart> lastMonthExpensesByWeekLineChart() {

        if (!userServiceImpl.checkCapability("CAP_VIEW_HOME")) {
            throw new AccessDeniedException(NO_PERMISSION_STATS_MONTH_BY_WEEK);
        }

        return ResponseEntity.ok(expensesServiceImpl.lastMonthExpensesByWeekLineChart());
    }

    @RequestMapping(value = "/statistics", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<HomeStatisticsView> statistics() {

        if (!userServiceImpl.checkCapability("CAP_VIEW_HOME")) {
            throw new AccessDeniedException(NO_PERMISSION_STATS_VIEW);
        }

        return ResponseEntity.ok(expensesServiceImpl.statistics());
    }
}
