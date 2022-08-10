package eu.zinovi.receipts.domain.scheduler;

import eu.zinovi.receipts.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@EnableScheduling
public class ScheduledStatistics {
    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduledStatistics.class);
    private final StatisticsService statisticsService;

    public ScheduledStatistics(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Scheduled(cron = "0 2 0 * * *")
    public void calculateStatistics() {
        LOGGER.info("Calculating statistics");
        statisticsService.calculateStatistics();
    }
}
