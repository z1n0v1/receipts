package eu.zinovi.receipts.domain.model.view.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AdminStatsView {

    private Integer todayReceiptsCount;
    private Integer todayUnprocessedReceiptsCount;
    private Integer todayErroneousReceiptsCount;

    private Integer lastDayReceiptsCount;
    private Integer lastDayUnprocessedReceiptsCount;
    private Integer lastDayErroneousReceiptsCount;
    private Integer lastWeekReceiptsCount;
    private Integer lastWeekUnprocessedReceiptsCount;
    private Integer lastWeekErroneousReceiptsCount;
    private Integer lastMonthReceiptsCount;
    private Integer lastMonthUnprocessedReceiptsCount;
    private Integer lastMonthErroneousReceiptsCount;
    private Long totalReceiptsCount;
    private Integer totalUnprocessedReceiptsCount;
    private Integer totalErroneousReceiptsCount;

    private BigDecimal todayReceiptsAmount;
    private BigDecimal lastDayReceiptsAmount;
    private BigDecimal lastWeekReceiptsAmount;
    private BigDecimal lastMonthReceiptsAmount;
    private BigDecimal totalReceiptsAmount;

    private Integer todayItemsCount;
    private Integer lastDayItemsCount;
    private Integer lastWeekItemsCount;
    private Integer lastMonthItemsCount;
    private Integer totalItemsCount;

    private Integer todayCompaniesCount;
    private Integer lastDayCompaniesCount;
    private Integer lastWeekCompaniesCount;
    private Integer lastMonthCompaniesCount;
    private Long totalCompaniesCount;

    private Integer todayStoresCount;
    private Integer lastDayStoresCount;
    private Integer lastWeekStoresCount;
    private Integer lastMonthStoresCount;
    private Long totalStoresCount;

    private Integer todayNewUsersCount;
    private Integer lastDayNewUsersCount;
    private Integer lastWeekNewUsersCount;
    private Integer lastMonthNewUsersCount;
    private Long totalUsersCount;

    private Integer todayDayActiveUsersCount;
    private Integer lastDayActiveUsersCount;
    private Integer lastWeekActiveUsersCount;
    private Integer lastMonthActiveUsersCount;

    private Integer todayDayNewGoogleUsersCount;
    private Integer lastDayNewGoogleUsersCount;
    private Integer lastWeekNewGoogleUsersCount;
    private Integer lastMonthNewGoogleUsersCount;
    private Integer totalGoogleUsersCount;

}
