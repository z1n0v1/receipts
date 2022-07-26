package eu.zinovi.receipts.repository;

import eu.zinovi.receipts.domain.model.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface StatisticsRepository extends JpaRepository<Statistics, UUID> {
    Optional<Statistics> findByDateStatistics(LocalDate date);

    Statistics findFirstByOrderByDateStatisticsDesc();
}