package eu.zinovi.receipts.repository;

import org.springframework.data.domain.Page;
import eu.zinovi.receipts.domain.model.entity.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    List<Item> findByReceiptIdOrderByPosition(UUID receiptId);
    Page<Item> findByReceiptIdAndNameContainingOrCategoryNameContaining(UUID receiptId, String name, String category,
                                                                        Pageable pageable);
    Page<Item> findByReceiptId(UUID receiptId, Pageable pageable);

    Optional<Item> findByReceiptIdAndPosition(UUID receiptId, Integer position);

    Integer deleteByReceiptIdAndPosition(UUID receiptId, Integer position);

    Integer countByReceiptDateOfPurchaseAfterAndReceiptDateOfPurchaseBefore(LocalDateTime start, LocalDateTime end);

//    Page<Item> findByReceiptId(UUID receiptId, Pageable pageable);

}