package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.entity.Company;
import eu.zinovi.receipts.domain.model.view.ReceiptCompanyByEikView;

import javax.transaction.Transactional;

public interface CompanyService {
    Company findByEik(String eik);

    @Transactional
    ReceiptCompanyByEikView receiptEikView(String eik);
}
