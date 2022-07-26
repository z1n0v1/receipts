package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.entity.Company;
import eu.zinovi.receipts.domain.model.mapper.CompanyToReceiptCompanyByEik;
import eu.zinovi.receipts.domain.model.view.ReceiptCompanyByEikView;
import eu.zinovi.receipts.domain.exception.EntityNotFoundException;
import eu.zinovi.receipts.repository.CompanyRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class CompanyService {
    private final CompanyToReceiptCompanyByEik companyToReceiptCompanyByEik;
    private final CompanyRepository companyRepository;
    private final AddressService addressService;

    public CompanyService(CompanyToReceiptCompanyByEik companyToReceiptCompanyByEik, CompanyRepository companyRepository, AddressService addressService) {
        this.companyToReceiptCompanyByEik = companyToReceiptCompanyByEik;
        this.companyRepository = companyRepository;
        this.addressService = addressService;
    }

    public Company findByEik(String eik) {
        Optional<Company> company = companyRepository.findByEik(eik);
        if (company.isPresent()) {
            return company.get();
        }

        // Scrape company registry for company details

        String companyName;
        String companyActivity;
        String companyAddress = null;

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

            /*
             companyAddress = companyInfoPage.select(
                            "body > div.container.inner-page > div:nth-child(2) > div > dl > dd:nth-child(20)")
                    .get(0).textNodes().get(0).text().replace("\n", " ");
             */

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

        Company companyEntity = new Company();
        companyEntity.setEik(eik);
        companyEntity.setName(companyName);
        companyEntity.setActivity(companyActivity);
        companyEntity.setAddress(addressService.getAddress(companyAddress));
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
