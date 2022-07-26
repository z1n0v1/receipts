package eu.zinovi.receipts.domain.model.binding.admin;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class AdminCategoryDeleteBindingModel {

    @NotNull(message = "Невалидно ID на категорията")
    @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
            message = "Невалидно ID на категорията")
    private String id;

}
