package eu.zinovi.receipts.service;

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
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ExpensesService {
    private final UserService userService;
    private final ReceiptRepository receiptRepository;

    public ExpensesService(UserService userService, ReceiptRepository receiptRepository) {
        this.userService = userService;
        this.receiptRepository = receiptRepository;
    }

    @Transactional
    public PieChart monthlyExpensesByCategoryPieChart() {

        Map<Category, BigDecimal> expenses = new LinkedHashMap<>();
        User user = userService.getCurrentUser();

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
                .setLabel("Месечни разходи по категории");
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

    @Transactional
    public PieChart totalExpensesByCategoryPieChart() {
        Map<Category, BigDecimal> expenses = new LinkedHashMap<>();
        User user = userService.getCurrentUser();

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
                .setLabel("Месечни разходи по категории");
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

    @Transactional
    public LineChart lastMonthExpensesByWeekLineChart() {
        Map<LocalDate, BigDecimal> expenses = new LinkedHashMap<>();
        User user = userService.getCurrentUser();



        for (int i = 4; i >= 1; i--) {
            BigDecimal weekExpenses = receiptRepository.sumByUserAndDateOfPurchaseAfterAndDateOfPurchaseBefore(
                    user, LocalDateTime.now().minusWeeks(i + 1), LocalDateTime.now().minusWeeks(i));
            expenses.put(LocalDate.now().minusWeeks(i), weekExpenses);
        }

        BigDecimal thisWeekExpenses = receiptRepository.sumByUserAndDateOfPurchaseAfterAndDateOfPurchaseBefore(
                user, LocalDateTime.now().minusWeeks(1), LocalDateTime.now());
        expenses.put(LocalDate.now(), thisWeekExpenses == null ? BigDecimal.ZERO : thisWeekExpenses);

        LineDataset dataset = new LineDataset()
                .setLabel("Разходи последния месец по седмици");
        LineData data = new LineData();

        for (Map.Entry<LocalDate, BigDecimal> entry : expenses.entrySet()) {
            data.addLabel(entry.getKey()
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            dataset.addData(entry.getValue() == null ? BigDecimal.ZERO : entry.getValue());
//            dataset.addBackgroundColor(Color.random());
        }
//        dataset.addBorderWidth(2);
        data.addDataset(dataset);

        return new LineChart(data);
    }

    @Transactional
    public HomeStatisticsView statistics() {
        HomeStatisticsView statistics = new HomeStatisticsView();
        User user = userService.getCurrentUser();

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
