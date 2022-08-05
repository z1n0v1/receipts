package eu.zinovi.receipts.util.impl;

import eu.zinovi.receipts.domain.exception.EntityNotFoundException;
import eu.zinovi.receipts.domain.model.service.CompanyRegisterBGApiServiceModel;
import eu.zinovi.receipts.util.RegisterBGApi;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RegisterBGPapagalScraper implements RegisterBGApi {
    @Override
    public CompanyRegisterBGApiServiceModel getCompanyInfo(String eik) throws EntityNotFoundException {
        CompanyRegisterBGApiServiceModel companyRegisterBGApiServiceModel = new CompanyRegisterBGApiServiceModel();

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

            companyRegisterBGApiServiceModel.setCompanyName( companyInfoPage.select(
                            "body > div.container.inner-page > div:nth-child(2) > div > h1")
                    .get(0).text());
            companyRegisterBGApiServiceModel.setCompanyActivity( companyInfoPage.select(
                            "span.full-subject-of-activity")
                    .get(0).text());

            /*
             companyAddress = companyInfoPage.select(
                            "body > div.container.inner-page > div:nth-child(2) > div > dl > dd:nth-child(20)")
                    .get(0).textNodes().get(0).text().replace("\n", " ");
             */

            for (Element element :companyInfoPage.select(
                    "body > div.container.inner-page > div:nth-child(2) > div > dl > *")) {
                if (element.text().equals("Седалище адрес")) {
                    companyRegisterBGApiServiceModel.setCompanyAddress(element.nextElementSibling().textNodes()
                            .get(0).text().replace("\n", " "));
                }
            }
        } catch (Exception e) {
            return null;
        }
        return companyRegisterBGApiServiceModel;
    }
}
