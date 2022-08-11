package eu.zinovi.receipts.service.impl;

import be.ceau.chart.LineChart;
import be.ceau.chart.PieChart;
import be.ceau.chart.data.LineData;
import be.ceau.chart.data.PieData;
import be.ceau.chart.dataset.LineDataset;
import be.ceau.chart.dataset.PieDataset;
import eu.zinovi.receipts.domain.model.entity.Category;
import eu.zinovi.receipts.domain.model.entity.Item;
import eu.zinovi.receipts.domain.model.entity.Receipt;
import eu.zinovi.receipts.domain.model.entity.User;
import eu.zinovi.receipts.domain.model.view.HomeStatisticsView;
import eu.zinovi.receipts.repository.ReceiptRepository;
import eu.zinovi.receipts.service.ExpensesService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static eu.zinovi.receipts.util.constants.LabelConstants.*;

@Service
public class ExpensesServiceImpl implements ExpensesService {
    private final UserServiceImpl userServiceImpl;
    private final ReceiptRepository receiptRepository;

    public ExpensesServiceImpl(UserServiceImpl userServiceImpl, ReceiptRepository receiptRepository) {
        this.userServiceImpl = userServiceImpl;
        this.receiptRepository = receiptRepository;
    }

    @Override
    @Transactional
    public PieChart monthlyExpensesByCategoryPieChart() {

        Map<Category, BigDecimal> expenses = new LinkedHashMap<>();
        User user = userServiceImpl.getCurrentUser();

        List<Receipt> receipts = receiptRepository.findAllByUserAndDateOfPurchaseAfterAndDateOfPurchaseBefore(
                user, LocalDateTime.now().minusMonths(1), LocalDateTime.now());

        for (Receipt receipt : receipts) {
            for (Item item : receipt.getItems()) {
                if (expenses.containsKey(item.getCategory())) {
                    expenses.put(item.getCategory(), expenses.get(item.getCategory()).add(item.getPrice()));
                } else {
                    expenses.put(item.getCategory(), item.getPrice());
                }
            }
        }
        PieDataset dataset = new PieDataset()
                .setLabel(EXPENSES_BY_CATEGORY_PIE_CHART_LABEL);
        PieData data = new PieData();

        for (Map.Entry<Category, BigDecimal> entry : expenses.entrySet()) {
            data.addLabel(entry.getKey().getName());
            dataset.addData(entry.getValue());
            dataset.addBackgroundColor(entry.getKey().getChartColor());
        }
        dataset.addBorderWidth(2);
        data.addDataset(dataset);

        return new PieChart(data);
    }

    @Override
    @Transactional
    public PieChart totalExpensesByCategoryPieChart() {
        Map<Category, BigDecimal> expenses = new LinkedHashMap<>();
        User user = userServiceImpl.getCurrentUser();

        List<Receipt> receipts = receiptRepository.findAllByUser(user);

        for (Receipt receipt : receipts) {
            for (Item item : receipt.getItems()) {
                if (expenses.containsKey(item.getCategory())) {
                    expenses.put(item.getCategory(), expenses.get(item.getCategory()).add(item.getPrice()));
                } else {
                    expenses.put(item.getCategory(), item.getPrice());
                }
            }
        }
        PieDataset dataset = new PieDataset()
                .setLabel(TOTAL_EXPENSES_BY_CATEGORY_PIE_CHART_LABEL);
        PieData data = new PieData();

        for (Map.Entry<Category, BigDecimal> entry : expenses.entrySet()) {
            data.addLabel(entry.getKey().getName());
            dataset.addData(entry.getValue());
            dataset.addBackgroundColor(entry.getKey().getChartColor());
        }
        dataset.addBorderWidth(2);
        data.addDataset(dataset);

        return new PieChart(data);

    }

    @Override
    @Transactional
    public LineChart lastMonthExpensesByWeekLineChart() {
        Map<LocalDate, BigDecimal> expenses = new LinkedHashMap<>();
        User user = userServiceImpl.getCurrentUser();

        for (int i = 4; i >= 1; i--) {
            BigDecimal weekExpenses = receiptRepository.sumByUserAndDateOfPurchaseAfterAndDateOfPurchaseBefore(
                    user, LocalDateTime.now().minusWeeks(i + 1), LocalDateTime.now().minusWeeks(i));
            expenses.put(LocalDate.now().minusWeeks(i), weekExpenses);
        }

        BigDecimal thisWeekExpenses = receiptRepository.sumByUserAndDateOfPurchaseAfterAndDateOfPurchaseBefore(
                user, LocalDateTime.now().minusWeeks(1), LocalDateTime.now());
        expenses.put(LocalDate.now(), thisWeekExpenses == null ? BigDecimal.ZERO : thisWeekExpenses);

        LineDataset dataset = new LineDataset()
                .setLabel(EXPENSES_BY_WEEK_LINE_CHART_LABEL);
        LineData data = new LineData();

        for (Map.Entry<LocalDate, BigDecimal> entry : expenses.entrySet()) {
            data.addLabel(entry.getKey()
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            dataset.addData(entry.getValue() == null ? BigDecimal.ZERO : entry.getValue());
        }
        data.addDataset(dataset);

        return new LineChart(data);
    }

    @Override
    @Transactional
    public HomeStatisticsView statistics() {
        HomeStatisticsView statistics = new HomeStatisticsView();
        User user = userServiceImpl.getCurrentUser();

        statistics.setLastMonthReceipts(receiptRepository.
                countByUserAndDateOfPurchaseAfterAndDateOfPurchaseBefore(user,
                        LocalDateTime.now().minusMonths(1),
                        LocalDateTime.now()));
        statistics.setTotalReceipts(receiptRepository.countByUser(user));
        statistics.setLastMonthExpenses(receiptRepository.
                sumByUserAndDateOfPurchaseAfterAndDateOfPurchaseBefore(user,
                        LocalDateTime.now().minusMonths(1),
                        LocalDateTime.now()));
        statistics.setTotalExpenses(receiptRepository.sumByUser(user));
        statistics.setNumCompanies(receiptRepository.countCompaniesByUserId(user.getId()));
        statistics.setNumStores(receiptRepository.countStoresByUserId(user.getId()));

        return statistics;
    }
}
