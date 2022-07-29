package eu.zinovi.receipts.domain.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor @Entity
@Table(name = "receipt_images", schema = "public")
public class ReceiptImage extends BaseEntity {

    @Column(name = "image_url", unique = true)
    private String imageUrl;

    @Column(name = "added_on", nullable = false)
    private LocalDateTime addedOn;

    @Column(name = "processed", nullable = false)
    private Boolean isProcessed;

    @OneToOne @ToString.Exclude
    Receipt receipt;

    @ManyToOne @ToString.Exclude
    User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ReceiptImage that = (ReceiptImage) o;
        return Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), imageUrl);
    }
}
