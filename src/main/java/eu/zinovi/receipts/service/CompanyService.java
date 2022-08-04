package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.entity.Company;
import eu.zinovi.receipts.domain.model.mapper.CompanyToReceiptCompanyByEik;
import eu.zinovi.receipts.domain.model.service.CompanyRegisterBGApiServiceModel;
import eu.zinovi.receipts.domain.model.view.ReceiptCompanyByEikView;
import eu.zinovi.receipts.domain.exception.EntityNotFoundException;
import eu.zinovi.receipts.repository.CompanyRepository;
import eu.zinovi.receipts.util.RegisterBGApi;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class CompanyService {
    private final CompanyToReceiptCompanyByEik companyToReceiptCompanyByEik;
    private final CompanyRepository companyRepository;
    private final AddressService addressService;
    private final RegisterBGApi registerBGApi;

    public CompanyService(CompanyToReceiptCompanyByEik companyToReceiptCompanyByEik, CompanyRepository companyRepository, AddressService addressService, RegisterBGApi registerBGApi) {
        this.companyToReceiptCompanyByEik = companyToReceiptCompanyByEik;
        this.companyRepository = companyRepository;
        this.addressService = addressService;
        this.registerBGApi = registerBGApi;
    }

    public Company findByEik(String eik) {
        Optional<Company> company = companyRepository.findByEik(eik);
        if (company.isPresent()) {
            return company.get();
        }

        // Scrape company registry for company details

//        String companyName;
//        String companyActivity;
//        String companyAddress = null;

        /*
        try {
            Document searchResult = Jsoup.connect(String.format(
                    "https://papagal.bg/search_results/%s?type=company", eik)).get();
            Elements elements = searchResult.select(
                    "body > div.container.inner-page > div:nth-child(2) > div > div.table-responsive >" +
                            " table > tbody > tr > td:nth-child(2) > a");
            if (elements.size() < 1) {
                return null;
            }
            String companyInfoUrl = elements.get(0).absUrl("href");
            Document companyInfoPage = Jsoup.connect(companyInfoUrl).get();

            companyName = companyInfoPage.select(
                            "body > div.container.inner-page > div:nth-child(2) > div > h1")
                    .get(0).text();
            companyActivity = companyInfoPage.select(
                            "span.full-subject-of-activity")
                    .get(0).text();

            for (Element element :companyInfoPage.select(
                    "body > div.container.inner-page > div:nth-child(2) > div > dl > *")) {
                if (element.text().equals("Седалище адрес")) {
                    companyAddress = element.nextElementSibling().textNodes()
                            .get(0).text().replace("\n", " ");
                }
            }
        } catch (Exception e) {
            return null;
        }
        */

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

    @Transactional
    public ReceiptCompanyByEikView receiptEikView(String eik) {
        Company company = findByEik(eik);
        if (company == null) {
            throw new EntityNotFoundException("Не е намерена компания с ЕИК " + eik);
        }
        return companyToReceiptCompanyByEik.map(company);
    }
}
