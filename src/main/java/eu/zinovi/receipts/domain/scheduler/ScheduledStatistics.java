package eu.zinovi.receipts.domain.scheduler;

import eu.zinovi.receipts.service.impl.StatisticsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@EnableScheduling
public class ScheduledStatistics {
    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduledStatistics.class);
    private final StatisticsServiceImpl statisticsServiceImpl;

    public ScheduledStatistics(StatisticsServiceImpl statisticsServiceImpl) {
        this.statisticsServiceImpl = statisticsServiceImpl;
    }

    @Scheduled(cron = "0 2 0 * * *")
    public void calculateStatistics() {
        LOGGER.info("Calculating statistics");
        statisticsServiceImpl.calculateStatistics();
    }
}
