package eu.zinovi.receipts.domain.model.view;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HomeStatisticsView {
    private BigDecimal lastMonthExpenses;
    private BigDecimal totalExpenses;
    private Integer lastMonthReceipts;
    private Integer totalReceipts;
    private Integer numCompanies;
    private Integer numStores;
}
