package eu.zinovi.receipts.util;

import eu.zinovi.receipts.domain.exception.EntityNotFoundException;
import eu.zinovi.receipts.domain.model.service.CompanyRegisterBGApiServiceModel;

public interface RegisterBGApi {
    CompanyRegisterBGApiServiceModel getCompanyInfo(String eik) throws EntityNotFoundException;
}
