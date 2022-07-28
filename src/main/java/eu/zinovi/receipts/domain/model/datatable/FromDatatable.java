package eu.zinovi.receipts.domain.model.datatable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


// TODO: Validate
@Data @NoArgsConstructor @AllArgsConstructor
public class FromDatatable {

    @NotNull(message = "Трябва да има идентификатор на заявката")
    private Long draw;

    @NotNull(message = "Трябва да има начало на списъка")
    @PositiveOrZero(message = "Началото на списъка трябва да е положително число")
    private Integer start;

    @NotNull(message = "Трябва да има дължина на списъка")
    @Positive(message = "Дължината на списъка трябва да е положително число")
    private Integer length;

    private String  searchValue;
    private String orderColumn;
    private String orderDir;

    @NotEmpty(message = "Таблицата тябва да има дефинирани колони")
    private DatatableColumn[] columns;

    private DatatableOrder[] order;
    private DatatableSearch search;

    public String[][] getSortOrder() {
        String[][] sortOrder = new String[this.getOrder().length][2];
        for (int i = 0; i < this.getOrder().length; i++) {
            for (int j = 0; j < this.getColumns().length; j++) {
                if (this.getColumns()[j].getData().equals(this.getOrder()[i].getColumn())) {
                    sortOrder[i][0] = this.getColumns()[j].getName();
                    sortOrder[i][1] = this.getOrder()[i].getDir();
                }
            }
        }
        return sortOrder;
    }
}
