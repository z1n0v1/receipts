package eu.zinovi.receipts.domain.model.binding.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminCategorySaveBindingModel {


    @NotNull(message = INVALID_CATEGORY_ID)
    @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
            message = INVALID_CATEGORY_ID)
    private String id;

    @NotNull(message = REQUIRED_CATEGORY_NAME)
    @Size(min = 3,message = REQUIRED_CATEGORY_NAME_MINIMUM_LENGTH)
    private String name;

    @NotNull(message = REQUIRED_CATEGORY_COLOR)
    @Pattern(regexp="^#([A-Fa-f0-9]{6})$",
            message = INVALID_CATEGORY_COLOR)
    private String color;

}
