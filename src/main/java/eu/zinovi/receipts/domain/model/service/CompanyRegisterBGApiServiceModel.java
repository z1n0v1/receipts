package eu.zinovi.receipts.domain.model.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class CompanyRegisterBGApiServiceModel {
    private String companyName;
    private String companyActivity;
    private String companyAddress;
}
