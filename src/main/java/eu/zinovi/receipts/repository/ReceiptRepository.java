package eu.zinovi.receipts.repository;

import eu.zinovi.receipts.domain.model.entity.Receipt;
import eu.zinovi.receipts.domain.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {

    List<Receipt> findAllByOrderByDateOfPurchaseDesc();

    List<Receipt> findByUserOrderByDateOfPurchaseDesc(User user);

    Integer countByDateOfPurchaseAfterAndDateOfPurchaseBefore(LocalDateTime from, LocalDateTime to);

    Integer countByUserAndDateOfPurchaseAfterAndDateOfPurchaseBefore(User user,
                                                                     LocalDateTime from,
                                                                     LocalDateTime to);

    Integer countByUser(User user);

    Integer countByDateOfPurchaseAfterAndDateOfPurchaseBeforeAndReceiptImageIsProcessed(
            LocalDateTime from, LocalDateTime to, boolean processed);

    Integer countByReceiptImageIsProcessed(boolean processed);

    @Query(value =
            "SELECT count(id) FROM receipts WHERE (date_of_purchase BETWEEN ?1 AND ?2) AND total != items_total",
            nativeQuery = true)
//    @Query("SELECT count(id) FROM Receipt r WHERE (dateOfPurchase BETWEEN ?1 AND ?2) AND r.total != r.itemsTotal")
    Integer countByDateAfterAndDateBeforeAndTotalNotEqualsItemsTotal(
            LocalDateTime from, LocalDateTime to);

    @Query(value = "SELECT count(id) FROM receipts WHERE total != items_total",
            nativeQuery = true)
    Integer countByTotalNotEqualItemsTotal();

    //    @Query(value = "SELECT sum(total) FROM receipts.public.receipts WHERE (date_of_purchase between ?1 and ?2)", nativeQuery = true)
    @Query("SELECT sum( total) FROM Receipt WHERE (dateOfPurchase between ?1 and ?2)")
    BigDecimal sumByTotalWhereDateAfterAndDateBefore(LocalDateTime from, LocalDateTime to);

    @Query("SELECT sum(total) from  Receipt")
    BigDecimal sumByTotal();

    // Check if the query is correct
    @Query(value = "SELECT count(distinct company_id) from receipts" +
            " where (date_of_purchase between ?1 and ?2)", nativeQuery = true)
//    @Query("SELECT count(company) from Receipt where (dateOfPurchase between ?1 and ?2) group by company")
    Integer countCompanies(LocalDateTime from, LocalDateTime to);

    @Query(value = "SELECT count(distinct company_id) from receipts AS r" +
            " where r.user_id = ?1", nativeQuery = true)
    Integer countCompaniesByUserId(UUID userId);

    @Query(value = "SELECT count(distinct store_id) from receipts" +
            " where date_of_purchase between ?1 and ?2", nativeQuery = true)
    Integer countStores(LocalDateTime from, LocalDateTime to);

    @Query(value = "SELECT count(distinct store_id) from receipts" +
            " where user_id = ?1", nativeQuery = true)
    Integer countStoresByUserId(UUID userId);

    Page<Receipt> findAllByCompanyNameContainingOrStoreNameContaining(String companyName, String storeName, Pageable pageable);

    Page<Receipt> findByUser(User user, Pageable pageable);

    Page<Receipt> findByUserAndCompanyNameContainingOrStoreNameContaining(User user,
                                                                          String companyName,
                                                                          String storeName,
                                                                          Pageable pageable);

    List<Receipt> findAllByUserAndDateOfPurchaseAfterAndDateOfPurchaseBefore
            (User user, LocalDateTime from, LocalDateTime to);

    List<Receipt> findAllByUser(User user);

    @Query("SELECT sum(total) FROM Receipt WHERE (dateOfPurchase between ?2 and ?3) AND user = ?1")
    BigDecimal sumByUserAndDateOfPurchaseAfterAndDateOfPurchaseBefore
            (User user, LocalDateTime minusMonths, LocalDateTime now);


    @Query("SELECT sum(total) FROM Receipt WHERE user = ?1")
    BigDecimal sumByUser(User user);
}