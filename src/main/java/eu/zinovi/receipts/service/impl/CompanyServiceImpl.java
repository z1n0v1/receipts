package eu.zinovi.receipts.service.impl;

import eu.zinovi.receipts.domain.model.entity.Company;
import eu.zinovi.receipts.domain.model.mapper.CompanyToReceiptCompanyByEik;
import eu.zinovi.receipts.domain.model.service.CompanyRegisterBGApiServiceModel;
import eu.zinovi.receipts.domain.model.view.ReceiptCompanyByEikView;
import eu.zinovi.receipts.domain.exception.EntityNotFoundException;
import eu.zinovi.receipts.repository.CompanyRepository;
import eu.zinovi.receipts.service.AddressService;
import eu.zinovi.receipts.service.CompanyService;
import eu.zinovi.receipts.util.RegisterBGApi;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyToReceiptCompanyByEik companyToReceiptCompanyByEik;
    private final CompanyRepository companyRepository;
    private final AddressService addressService;
    private final RegisterBGApi registerBGApi;

    public CompanyServiceImpl(
            CompanyToReceiptCompanyByEik companyToReceiptCompanyByEik,
            CompanyRepository companyRepository,
            AddressService addressService,
            RegisterBGApi registerBGApi) {
        this.companyToReceiptCompanyByEik = companyToReceiptCompanyByEik;
        this.companyRepository = companyRepository;
        this.addressService = addressService;
        this.registerBGApi = registerBGApi;
    }

    @Override
    public Company findByEik(String eik) {
        Optional<Company> company = companyRepository.findByEik(eik);
        if (company.isPresent()) {
            return company.get();
        }

        CompanyRegisterBGApiServiceModel companyRegisterBGApiServiceModel = registerBGApi.getCompanyInfo(eik);

        if (companyRegisterBGApiServiceModel == null) {
            return null;
        }

        Company companyEntity = new Company();
        companyEntity.setEik(eik);
        companyEntity.setName(companyRegisterBGApiServiceModel.getCompanyName());
        companyEntity.setActivity(companyRegisterBGApiServiceModel.getCompanyActivity());
        companyEntity.setAddress(addressService.getAddress(companyRegisterBGApiServiceModel.getCompanyAddress()));
        companyRepository.save(companyEntity);
        return companyEntity;
    }

    @Override
    @Transactional
    public ReceiptCompanyByEikView receiptEikView(String eik) {
        Company company = findByEik(eik);
        if (company == null) {
            throw new EntityNotFoundException("Не е намерена компания с ЕИК " + eik);
        }
        return companyToReceiptCompanyByEik.map(company);
    }
}
