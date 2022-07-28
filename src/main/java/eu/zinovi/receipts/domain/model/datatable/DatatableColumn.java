package eu.zinovi.receipts.domain.model.datatable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DatatableColumn {
    private String data;
    private String name;
    private String searchable;
    private String orderable;
    private DatatableSearch search;
}
