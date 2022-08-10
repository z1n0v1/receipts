package eu.zinovi.receipts.service.impl;

import eu.zinovi.receipts.domain.model.entity.Statistics;
import eu.zinovi.receipts.domain.model.mapper.StatisticsToView;
import eu.zinovi.receipts.domain.model.view.admin.AdminStatsView;
import eu.zinovi.receipts.domain.model.view.IndexStatisticsView;
import eu.zinovi.receipts.repository.*;
import eu.zinovi.receipts.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsToView statisticsToView;
    private final StatisticsRepository statisticsRepository;
    private final ItemRepository itemRepository;
    private final ReceiptRepository receiptRepository;
    private final CompanyRepository companyRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;


    public StatisticsServiceImpl(StatisticsToView statisticsToView, StatisticsRepository statisticsRepository, ItemRepository itemRepository, ReceiptRepository receiptRepository, CompanyRepository companyRepository, StoreRepository storeRepository, UserRepository userRepository) {
        this.statisticsToView = statisticsToView;
        this.statisticsRepository = statisticsRepository;
        this.itemRepository = itemRepository;
        this.receiptRepository = receiptRepository;
        this.companyRepository = companyRepository;
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AdminStatsView getStatistics() {
        Statistics statistics = statisticsRepository.findFirstByOrderByDateStatisticsDesc();
        if (statistics == null) {
            return null;
        }
        AdminStatsView adminStatsView = statisticsToView.map(statistics);

        adminStatsView.setTodayReceiptsCount(getReceiptsCount(
                LocalDate.now().atStartOfDay(),
                LocalDateTime.now()));
        if (adminStatsView.getTodayReceiptsCount() == null) {
            adminStatsView.setTodayReceiptsCount(0);
        }

        adminStatsView.setTodayUnprocessedReceiptsCount(getUnprocessedReceiptsCount(
                LocalDate.now().atStartOfDay(),
                LocalDateTime.now()));
        if (adminStatsView.getTodayUnprocessedReceiptsCount() == null) {
            adminStatsView.setTodayUnprocessedReceiptsCount(0);
        }

        adminStatsView.setTodayErroneousReceiptsCount(getErroneousReceiptsCount(
                LocalDate.now().atStartOfDay(),
                LocalDateTime.now()));
        if (adminStatsView.getTodayErroneousReceiptsCount() == null) {
            adminStatsView.setTodayErroneousReceiptsCount(0);
        }

        adminStatsView.setTodayReceiptsAmount(getReceiptsAmount(
                LocalDate.now().atStartOfDay(),
                LocalDateTime.now()));
        if (adminStatsView.getTodayReceiptsAmount() == null) {
            adminStatsView.setTodayReceiptsAmount(BigDecimal.ZERO);
        }

        adminStatsView.setTodayItemsCount(getItemsCount(
                LocalDate.now().atStartOfDay(),
                LocalDateTime.now()));
        if (adminStatsView.getTodayItemsCount() == null) {
            adminStatsView.setTodayItemsCount(0);
        }

        adminStatsView.setTodayCompaniesCount(getCompaniesCount(
                LocalDate.now().atStartOfDay(),
                LocalDateTime.now()));
        if (adminStatsView.getTodayCompaniesCount() == null) {
            adminStatsView.setTodayCompaniesCount(0);
        }

        adminStatsView.setTodayStoresCount(getStoresCount(
                LocalDate.now().atStartOfDay(),
                LocalDateTime.now()));
        if (adminStatsView.getTodayStoresCount() == null) {
            adminStatsView.setTodayStoresCount(0);
        }

        adminStatsView.setTodayNewUsersCount(getNewUsersCount(
                LocalDate.now().atStartOfDay(),
                LocalDateTime.now()));
        if (adminStatsView.getTodayNewUsersCount() == null) {
            adminStatsView.setTodayNewUsersCount(0);
        }

        adminStatsView.setTodayDayActiveUsersCount(getActiveUsersCount(
                LocalDate.now().atStartOfDay(),
                LocalDateTime.now()));
        if (adminStatsView.getTodayDayActiveUsersCount() == null) {
            adminStatsView.setTodayDayActiveUsersCount(0);
        }

        adminStatsView.setTodayDayNewGoogleUsersCount(getNewGoogleUsersCount(
                LocalDate.now().atStartOfDay(),
                LocalDateTime.now()));
        if (adminStatsView.getTodayDayNewGoogleUsersCount() == null) {
            adminStatsView.setTodayDayNewGoogleUsersCount(0);
        }

        return adminStatsView;
    }

    @Override
    public void calculateStatistics() {

        Statistics statistics = statisticsRepository.findByDateStatistics(LocalDate.now().minusDays(1)).orElse(null);

        if (statistics == null) {
            statistics = new Statistics();
            statistics.setDateStatistics(LocalDate.now().minusDays(1));

            statistics.setLastDayReceiptsCount(getReceiptsCount(
                    LocalDate.now().minusDays(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastDayReceiptsCount() == null) {
                statistics.setLastDayReceiptsCount(0);
            }
            statistics.setLastDayUnprocessedReceiptsCount(getUnprocessedReceiptsCount(
                    LocalDate.now().minusDays(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastDayUnprocessedReceiptsCount() == null) {
                statistics.setLastDayUnprocessedReceiptsCount(0);
            }
            statistics.setLastDayErroneousReceiptsCount(getErroneousReceiptsCount(
                    LocalDate.now().minusDays(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastDayErroneousReceiptsCount() == null) {
                statistics.setLastDayErroneousReceiptsCount(0);
            }
            statistics.setLastWeekReceiptsCount(getReceiptsCount(
                    LocalDate.now().minusWeeks(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastWeekReceiptsCount() == null) {
                statistics.setLastWeekReceiptsCount(0);
            }
            statistics.setLastWeekUnprocessedReceiptsCount(getUnprocessedReceiptsCount(
                    LocalDate.now().minusWeeks(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastWeekUnprocessedReceiptsCount() == null) {
                statistics.setLastWeekUnprocessedReceiptsCount(0);
            }
            statistics.setLastWeekErroneousReceiptsCount(getErroneousReceiptsCount(
                    LocalDate.now().minusWeeks(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastWeekErroneousReceiptsCount() == null) {
                statistics.setLastWeekErroneousReceiptsCount(0);
            }
            statistics.setLastMonthReceiptsCount(getReceiptsCount(
                    LocalDate.now().minusMonths(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastMonthReceiptsCount() == null) {
                statistics.setLastMonthReceiptsCount(0);
            }
            statistics.setLastMonthUnprocessedReceiptsCount(getUnprocessedReceiptsCount(
                    LocalDate.now().minusMonths(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastMonthUnprocessedReceiptsCount() == null) {
                statistics.setLastMonthUnprocessedReceiptsCount(0);
            }
            statistics.setLastMonthErroneousReceiptsCount(getErroneousReceiptsCount(
                    LocalDate.now().minusMonths(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastMonthErroneousReceiptsCount() == null) {
                statistics.setLastMonthErroneousReceiptsCount(0);
            }
            statistics.setTotalReceiptsCount(getTotalReceiptsCount());
            if (statistics.getTotalReceiptsCount() == null) {
                statistics.setTotalReceiptsCount(0L);
            }
            statistics.setTotalUnprocessedReceiptsCount(getTotalUnprocessedReceiptsCount());
            if (statistics.getTotalUnprocessedReceiptsCount() == null) {
                statistics.setTotalUnprocessedReceiptsCount(0);
            }
            statistics.setTotalErroneousReceiptsCount(getTotalErroneousReceiptsCount());
            if (statistics.getTotalErroneousReceiptsCount() == null) {
                statistics.setTotalErroneousReceiptsCount(0);
            }

            statistics.setLastDayReceiptsAmount(getReceiptsAmount(
                    LocalDate.now().minusDays(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastDayReceiptsAmount() == null) {
                statistics.setLastDayReceiptsAmount(BigDecimal.ZERO);
            }
            statistics.setLastWeekReceiptsAmount(getReceiptsAmount(
                    LocalDate.now().minusWeeks(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastWeekReceiptsAmount() == null) {
                statistics.setLastWeekReceiptsAmount(BigDecimal.ZERO);
            }
            statistics.setLastMonthReceiptsAmount(getReceiptsAmount(
                    LocalDate.now().minusMonths(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastMonthReceiptsAmount() == null) {
                statistics.setLastMonthReceiptsAmount(BigDecimal.ZERO);
            }
            statistics.setTotalReceiptsAmount(getTotalReceiptsAmount());
            if (statistics.getTotalReceiptsAmount() == null) {
                statistics.setTotalReceiptsAmount(BigDecimal.ZERO);
            }

            statistics.setLastDayItemsCount(getItemsCount(
                    LocalDate.now().minusDays(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastDayItemsCount() == null) {
                statistics.setLastDayItemsCount(0);
            }
            statistics.setLastWeekItemsCount(getItemsCount(
                    LocalDate.now().minusWeeks(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastWeekItemsCount() == null) {
                statistics.setLastWeekItemsCount(0);
            }
            statistics.setLastMonthItemsCount(getItemsCount(
                    LocalDate.now().minusMonths(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastMonthItemsCount() == null) {
                statistics.setLastMonthItemsCount(0);
            }
            statistics.setTotalItemsCount(getTotalItemsCount());
            if (statistics.getTotalItemsCount() == null) {
                statistics.setTotalItemsCount(0L);
            }

            statistics.setLastDayCompaniesCount(getCompaniesCount(
                    LocalDate.now().minusDays(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastDayCompaniesCount() == null) {
                statistics.setLastDayCompaniesCount(0);
            }
            statistics.setLastWeekCompaniesCount(getCompaniesCount(
                    LocalDate.now().minusWeeks(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastWeekCompaniesCount() == null) {
                statistics.setLastWeekCompaniesCount(0);
            }
            statistics.setLastMonthCompaniesCount(getCompaniesCount(
                    LocalDate.now().minusMonths(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastMonthCompaniesCount() == null) {
                statistics.setLastMonthCompaniesCount(0);
            }
            statistics.setTotalCompaniesCount(getTotalCompaniesCount());
            if (statistics.getTotalCompaniesCount() == null) {
                statistics.setTotalCompaniesCount(0L);
            }

            statistics.setLastDayStoresCount(getStoresCount(
                    LocalDate.now().minusDays(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastDayStoresCount() == null) {
                statistics.setLastDayStoresCount(0);
            }
            statistics.setLastWeekStoresCount(getStoresCount(
                    LocalDate.now().minusWeeks(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastWeekStoresCount() == null) {
                statistics.setLastWeekStoresCount(0);
            }
            statistics.setLastMonthStoresCount(getStoresCount(
                    LocalDate.now().minusMonths(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastMonthStoresCount() == null) {
                statistics.setLastMonthStoresCount(0);
            }
            statistics.setTotalStoresCount(getTotalStoresCount());
            if (statistics.getTotalStoresCount() == null) {
                statistics.setTotalStoresCount(0L);
            }

            statistics.setLastDayNewUsersCount(getNewUsersCount(
                    LocalDate.now().minusDays(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastDayNewUsersCount() == null) {
                statistics.setLastDayNewUsersCount(0);
            }
            statistics.setLastWeekNewUsersCount(getNewUsersCount(
                    LocalDate.now().minusWeeks(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastWeekNewUsersCount() == null) {
                statistics.setLastWeekNewUsersCount(0);
            }
            statistics.setLastMonthNewUsersCount(getNewUsersCount(
                    LocalDate.now().minusMonths(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastMonthNewUsersCount() == null) {
                statistics.setLastMonthNewUsersCount(0);
            }
            statistics.setTotalUsersCount(getTotalUsersCount());

            statistics.setLastDayActiveUsersCount(getActiveUsersCount(
                    LocalDate.now().minusDays(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastDayActiveUsersCount() == null) {
                statistics.setLastDayActiveUsersCount(0);
            }
            statistics.setLastWeekActiveUsersCount(getActiveUsersCount(
                    LocalDate.now().minusWeeks(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastWeekActiveUsersCount() == null) {
                statistics.setLastWeekActiveUsersCount(0);
            }
            statistics.setLastMonthActiveUsersCount(getActiveUsersCount(
                    LocalDate.now().minusMonths(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastMonthActiveUsersCount() == null) {
                statistics.setLastMonthActiveUsersCount(0);
            }

            statistics.setLastDayNewGoogleUsersCount(getNewGoogleUsersCount(
                    LocalDate.now().minusDays(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastDayNewGoogleUsersCount() == null) {
                statistics.setLastDayNewGoogleUsersCount(0);
            }
            statistics.setLastWeekNewGoogleUsersCount(getNewGoogleUsersCount(
                    LocalDate.now().minusWeeks(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastWeekNewGoogleUsersCount() == null) {
                statistics.setLastWeekNewGoogleUsersCount(0);
            }
            statistics.setLastMonthNewGoogleUsersCount(getNewGoogleUsersCount(
                    LocalDate.now().minusMonths(1).atStartOfDay(),
                    LocalDate.now().atStartOfDay()));
            if (statistics.getLastMonthNewGoogleUsersCount() == null) {
                statistics.setLastMonthNewGoogleUsersCount(0);
            }
            statistics.setTotalGoogleUsersCount(getTotalGoogleUsersCount());
            if (statistics.getTotalGoogleUsersCount() == null) {
                statistics.setTotalGoogleUsersCount(0);
            }
            statisticsRepository.save(statistics);
        }
    }

    private Long getTotalUsersCount() {
        return userRepository.count();
    }

    private Integer getTotalGoogleUsersCount() {
        return userRepository.countByGoogleIdIsNotNull();
    }

    private Integer getNewGoogleUsersCount(LocalDateTime from, LocalDateTime to) {
        return userRepository.countByGoogleIdIsNotNullAndRegisteredOnBetween(from, to);
    }

    private Integer getActiveUsersCount(LocalDateTime from, LocalDateTime to) {
        return userRepository.countByLastSeenAfterAndLastSeenBefore(from, to);
    }

    private Integer getNewUsersCount(LocalDateTime from, LocalDateTime to) {
        return userRepository.countByRegisteredOnBetween(from, to);
    }

    private Long getTotalStoresCount() {
        return storeRepository.count();
    }

    private Integer getStoresCount(LocalDateTime from, LocalDateTime to) {
        return receiptRepository.countStores(from, to);
    }

    private Long getTotalCompaniesCount() {
        return companyRepository.count();
    }

    private Integer getCompaniesCount(LocalDateTime from, LocalDateTime to) {
        return receiptRepository.countCompanies(from, to);
    }

    private Long getTotalItemsCount() {
        return itemRepository.count();
    }

    private Integer getItemsCount(LocalDateTime start, LocalDateTime end) {
        return itemRepository.countByReceiptDateOfPurchaseAfterAndReceiptDateOfPurchaseBefore(start, end);
    }

    private BigDecimal getTotalReceiptsAmount() {
        BigDecimal amount = receiptRepository.sumByTotal();
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        return amount;
    }

    private BigDecimal getReceiptsAmount(LocalDateTime from, LocalDateTime to) {
        BigDecimal amount = receiptRepository.sumByTotalWhereDateAfterAndDateBefore(from, to);
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        return amount; // should check if this is correct
    }

    private Integer getTotalErroneousReceiptsCount() {
        return receiptRepository.countByTotalNotEqualItemsTotal();
    }

    private Integer getTotalUnprocessedReceiptsCount() {
        return receiptRepository.countByReceiptImageIsProcessed(false);
    }

    private Long getTotalReceiptsCount() {
        return receiptRepository.count();
    }

    private int getErroneousReceiptsCount(LocalDateTime from, LocalDateTime to) {
        return receiptRepository.countByDateAfterAndDateBeforeAndTotalNotEqualsItemsTotal(from, to);
    }

    private int getUnprocessedReceiptsCount(LocalDateTime from, LocalDateTime to) {
        return receiptRepository
                .countByDateOfPurchaseAfterAndDateOfPurchaseBeforeAndReceiptImageIsProcessed(from, to, false);
    }

    private int getReceiptsCount(LocalDateTime from, LocalDateTime to) {
        return receiptRepository.countByDateOfPurchaseAfterAndDateOfPurchaseBefore(from, to);
    }

    @Override
    public IndexStatisticsView getIndexStatistics() {
        IndexStatisticsView statistics = new IndexStatisticsView();

        statistics.setNumUsers(getTotalUsersCount());
        statistics.setNumReceipts(getTotalReceiptsCount());
        statistics.setNumCompanies(getTotalCompaniesCount());

        statistics.setTodayReceiptsSum(getReceiptsAmount(LocalDate.now().atStartOfDay(), LocalDateTime.now()));
        statistics.setTotalReceiptsSum(getTotalReceiptsAmount());

        return statistics;

    }
}
