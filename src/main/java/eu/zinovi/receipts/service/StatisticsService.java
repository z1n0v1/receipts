package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.view.IndexStatisticsView;
import eu.zinovi.receipts.domain.model.view.admin.AdminStatsView;

public interface StatisticsService {
    AdminStatsView getStatistics();

    void calculateStatistics();

    IndexStatisticsView getIndexStatistics();
}
