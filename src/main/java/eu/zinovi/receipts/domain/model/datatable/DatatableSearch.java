package eu.zinovi.receipts.domain.model.datatable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DatatableSearch {
    private String value;
    private String regex;
}
