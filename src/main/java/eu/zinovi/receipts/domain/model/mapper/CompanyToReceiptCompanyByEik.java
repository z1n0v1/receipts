package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.entity.Company;
import eu.zinovi.receipts.domain.model.view.ReceiptCompanyByEikView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CompanyToReceiptCompanyByEik {
    @Mappings({
            @Mapping(source = "name", target = "companyName"),
            @Mapping(source = "address.value", target = "companyAddress")
    })
    ReceiptCompanyByEikView map(Company company);
}
