package eu.zinovi.receipts.service;

import be.ceau.chart.LineChart;
import be.ceau.chart.PieChart;
import eu.zinovi.receipts.domain.model.view.HomeStatisticsView;

import javax.transaction.Transactional;

public interface ExpensesService {
    @Transactional
    PieChart monthlyExpensesByCategoryPieChart();

    @Transactional
    PieChart totalExpensesByCategoryPieChart();

    @Transactional
    LineChart lastMonthExpensesByWeekLineChart();

    @Transactional
    HomeStatisticsView statistics();
}
