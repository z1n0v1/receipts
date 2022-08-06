package eu.zinovi.receipts.domain.model.binding.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static eu.zinovi.receipts.util.constants.MessageConstants.INVALID_CATEGORY_ID;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminCategoryDeleteBindingModel {

    @NotNull(message = INVALID_CATEGORY_ID)
    @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
            message = INVALID_CATEGORY_ID)
    private String id;

}
