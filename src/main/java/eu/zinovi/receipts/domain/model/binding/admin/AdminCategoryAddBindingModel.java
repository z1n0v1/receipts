package eu.zinovi.receipts.domain.model.binding.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminCategoryAddBindingModel {
    @NotNull(message = REQUIRED_CATEGORY_NAME)
    @Size(min = 3,message = REQUIRED_CATEGORY_NAME_MINIMUM_LENGTH)
    private String name;

    @NotNull(message = REQUIRED_CATEGORY_COLOR)
    @Pattern(regexp="^#([A-Fa-f0-9]{6})$",
            message = INVALID_CATEGORY_COLOR)
    private String color;
}
