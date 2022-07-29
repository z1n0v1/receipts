package eu.zinovi.receipts.domain.model.entity;

import be.ceau.chart.color.Color;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

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
        items = new ArrayList<>();
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
}
