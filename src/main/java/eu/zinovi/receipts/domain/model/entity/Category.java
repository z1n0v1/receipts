package eu.zinovi.receipts.domain.model.entity;

import be.ceau.chart.color.Color;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@Entity
@RequiredArgsConstructor
@Table(name = "categories")
public class Category extends BaseEntity {

    @NonNull
    @Column(name = "name", nullable = false)
    private String name;

    @NonNull
    @Column(name = "color", nullable = false)
    private String color;

    @OneToMany(mappedBy = "category")
    private Collection<Item> items;

    public Color getChartColor() {
        return new Color(Integer.valueOf(this.color.substring(1, 3), 16),
                Integer.valueOf(this.color.substring(3, 5), 16),
                Integer.valueOf(this.color.substring(5, 7), 16));
    }
}
