package eu.zinovi.receipts.domain.model.view.admin;

import lombok.Data;

@Data
public class AdminCategoryView {
    private String id;
    private String name;
    private String color;
    private int itemsCount;
}
