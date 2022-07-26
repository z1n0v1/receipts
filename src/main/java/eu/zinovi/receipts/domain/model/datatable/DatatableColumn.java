package eu.zinovi.receipts.domain.model.datatable;

import lombok.Data;

@Data
public class DatatableColumn {
    private String data;
    private String name;
    private String searchable;
    private String orderable;
    private DatatableSearch search;
}
