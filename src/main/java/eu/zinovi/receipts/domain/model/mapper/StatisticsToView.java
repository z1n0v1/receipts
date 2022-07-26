package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.entity.Statistics;
import eu.zinovi.receipts.domain.model.view.admin.AdminStatsView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatisticsToView {
        AdminStatsView map(Statistics statistics);

}
