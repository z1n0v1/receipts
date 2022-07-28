package eu.zinovi.receipts.domain.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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
}
