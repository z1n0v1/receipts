package eu.zinovi.receipts.domain.model.binding.admin;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
public class AdminCategorySaveBindingModel {


    @NotNull(message = "Невалидно ID на категорията")
    @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
            message = "Невалидно ID на категорията")
    private String id;

    @NotNull(message = "Името на категорията е задължително")
    @Size(min = 3,message = "Името на категорията трябва да е поне 3 символа")
    private String name;

    @NotNull(message = "Цвят на категорията е задължителен")
    @Pattern(regexp="^#([A-Fa-f0-9]{6})$",
            message = "Невалиден цвят на категорията")
    private String color;

}
