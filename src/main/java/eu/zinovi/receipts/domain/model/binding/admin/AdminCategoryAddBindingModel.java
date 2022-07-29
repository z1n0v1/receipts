package eu.zinovi.receipts.domain.model.binding.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminCategoryAddBindingModel {
    @NotNull(message = "Името на категорията е задължително")
    @Size(min = 3,message = "Името на категорията трябва да е поне 3 символа")
    private String name;

    @NotNull(message = "Цвят на категорията е задължителен")
    @Pattern(regexp="^#([A-Fa-f0-9]{6})$",
            message = "Невалиден цвят на категорията")
    private String color;
}
