package eu.zinovi.receipts.domain.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "statistics")
public class Statistics extends BaseEntity {

    @Column(name = "date_statistcs", nullable = false, unique = true)
    private LocalDate dateStatistics;

    @Column(name = "last_day_receipts_count", nullable = false)
    private Integer lastDayReceiptsCount;
    @Column(name = "last_day_unprocessed_receipts_count", nullable = false)
    private Integer lastDayUnprocessedReceiptsCount;
    @Column(name = "last_day_erronous_receipts_count", nullable = false)
    private Integer lastDayErroneousReceiptsCount;
    @Column(name = "last_week_receipts_count", nullable = false)
    private Integer lastWeekReceiptsCount;
    @Column(name = "last_week_unprocessed_receipts_count", nullable = false)
    private Integer lastWeekUnprocessedReceiptsCount;
    @Column(name = "last_week_erronous_receipts_count", nullable = false)
    private Integer lastWeekErroneousReceiptsCount;
    @Column(name = "last_month_receipts_count", nullable = false)
    private Integer lastMonthReceiptsCount;
    @Column(name = "last_month_unprocessed_receipts_count", nullable = false)
    private Integer lastMonthUnprocessedReceiptsCount;
    @Column(name = "last_month_erronous_receipts_count", nullable = false)
    private Integer lastMonthErroneousReceiptsCount;
    @Column(name = "total_receipts_count", nullable = false)
    private Long totalReceiptsCount;
    @Column(name = "total_unprocessed_receipts_count", nullable = false)
    private Integer totalUnprocessedReceiptsCount;
    @Column(name = "total_erronous_receipts_count", nullable = false)
    private Integer totalErroneousReceiptsCount;

    @Column(name = "last_day_receipts_amount", nullable = false)
    private BigDecimal lastDayReceiptsAmount;
    @Column(name = "last_week_receipts_amount", nullable = false)
    private BigDecimal lastWeekReceiptsAmount;
    @Column(name = "last_month_receipts_amount", nullable = false)
    private BigDecimal lastMonthReceiptsAmount;
    @Column(name = "total_receipts_amount", nullable = false)
    private BigDecimal totalReceiptsAmount;

    @Column(name = "last_day_items_count", nullable = false)
    private Integer lastDayItemsCount;
    @Column(name = "last_week_items_count", nullable = false)
    private Integer lastWeekItemsCount;
    @Column(name = "last_month_items_count", nullable = false)
    private Integer lastMonthItemsCount;
    @Column(name = "total_items_count", nullable = false)
    private Long totalItemsCount;

    @Column(name = "last_day_companies_count", nullable = false)
    private Integer lastDayCompaniesCount;
    @Column(name = "last_week_companies_count", nullable = false)
    private Integer lastWeekCompaniesCount;
    @Column(name = "last_month_companies_count", nullable = false)
    private Integer lastMonthCompaniesCount;
    @Column(name = "total_companies_count", nullable = false)
    private Long totalCompaniesCount;

    @Column(name = "last_day_stores_count", nullable = false)
    private Integer lastDayStoresCount;
    @Column(name = "last_week_stores_count", nullable = false)
    private Integer lastWeekStoresCount;
    @Column(name = "last_month_stores_count", nullable = false)
    private Integer lastMonthStoresCount;
    @Column(name = "total_stores_count", nullable = false)
    private Long totalStoresCount;

    @Column(name = "last_day_new_users_count", nullable = false)
    private Integer lastDayNewUsersCount;
    @Column(name = "last_week_new_users_count", nullable = false)
    private Integer lastWeekNewUsersCount;
    @Column(name = "last_month_new_users_count", nullable = false)
    private Integer lastMonthNewUsersCount;
    @Column(name = "total_users_count", nullable = false)
    private Long totalUsersCount;

    @Column(name = "last_day_active_users_count", nullable = false)
    private Integer lastDayActiveUsersCount;
    @Column(name = "last_week_active_users_count", nullable = false)
    private Integer lastWeekActiveUsersCount;
    @Column(name = "last_month_active_users_count", nullable = false)
    private Integer lastMonthActiveUsersCount;

    @Column(name = "last_day_new_google_users_count", nullable = false)
    private Integer lastDayNewGoogleUsersCount;
    @Column(name = "last_week_new_google_users_count", nullable = false)
    private Integer lastWeekNewGoogleUsersCount;
    @Column(name = "last_month_new_google_users_count", nullable = false)
    private Integer lastMonthNewGoogleUsersCount;
    @Column(name = "total_google_users_count", nullable = false)
    private Integer totalGoogleUsersCount;

}
