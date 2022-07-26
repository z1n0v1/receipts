package eu.zinovi.receipts.repository;

import eu.zinovi.receipts.domain.model.entity.ReceiptImage;
import eu.zinovi.receipts.domain.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReceiptImageRepository extends JpaRepository<ReceiptImage, UUID> {
    List<ReceiptImage> findByUser(User currentUser);

    List<ReceiptImage> findByIsProcessed(boolean b);

}