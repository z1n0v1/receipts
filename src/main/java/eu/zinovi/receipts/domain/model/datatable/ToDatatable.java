package eu.zinovi.receipts.domain.model.datatable;

import lombok.Data;

@Data
public class ToDatatable {
    private Long draw;
    private Long recordsTotal;
    private Long recordsFiltered;
    private String[][] data;
    private String error;
}
