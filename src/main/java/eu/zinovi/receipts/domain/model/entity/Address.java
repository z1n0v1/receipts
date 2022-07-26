package eu.zinovi.receipts.domain.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Getter @Setter @ToString @NoArgsConstructor @Entity
@Table(name = "addresses", schema = "public", indexes = {
        @Index(columnList = "value", unique = true)
})
public class Address extends BaseEntity {

    @Column(name = "value", nullable = false, unique = true)
    private String value;

    @Column(name = "city")
    private String city;

    @Column(name = "street")
    private String street;
}
