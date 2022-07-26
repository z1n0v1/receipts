package eu.zinovi.receipts.domain.model.view;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IndexStatisticsView {
    private Long numUsers;
    private Long numReceipts;
    private Long numCompanies;
    private BigDecimal totalReceiptsSum;
    private BigDecimal todayReceiptsSum;
}
