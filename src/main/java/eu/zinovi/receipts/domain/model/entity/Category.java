package eu.zinovi.receipts.domain.model.entity;

import be.ceau.chart.color.Color;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

@Getter
@Setter
@Entity
@ToString
@Table(name = "categories")
public class Category extends BaseEntity {

    @NonNull
    @Column(name = "name", nullable = false)
    private String name;

    @NonNull
    @Column(name = "color", nullable = false)
    private String color;

    @OneToMany(mappedBy = "category") @ToString.Exclude
    private Collection<Item> items;

    public Category() {
        items = new HashSet<>();
    }

    public Category(String name, String color) {
        this();
        this.name = name;
        this.color = color;
    }

    public Color getChartColor() {
        return new Color(Integer.valueOf(this.color.substring(1, 3), 16),
                Integer.valueOf(this.color.substring(3, 5), 16),
                Integer.valueOf(this.color.substring(5, 7), 16));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name) && Objects.equals(color, category.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, color);
    }
}
