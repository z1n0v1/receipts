package eu.zinovi.receipts.repository;

import eu.zinovi.receipts.domain.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);

    Optional<User> getByEmail(String name);

    Integer countByRegisteredOnBetween(LocalDateTime from, LocalDateTime to);

    Integer countByLastSeenAfterAndLastSeenBefore(LocalDateTime from, LocalDateTime to);

//    @Query(value = "SELECT count(id) FROM receipts.public.users " +
//            "WHERE google_id IS NOT NULL AND registered_on BETWEEN ?1 AND ?2", nativeQuery = true)
//    Integer countByGoogleBetween(LocalDateTime from, LocalDateTime to);

    Integer countByGoogleIdIsNotNullAndRegisteredOnBetween(LocalDateTime from, LocalDateTime to);

    Integer countByGoogleIdIsNotNull();


    @Query(value = "SELECT sum (total) FROM receipts " +
            "WHERE user_id = ?1", nativeQuery = true)
    BigDecimal sumByReceiptsTotalByUser(UUID userId);

    Page<User> findAllByEmailContaining(String email, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByEmailAndEmailVerified(String email, boolean verified);

    User getUserByEmail(String email);

    void deleteByEmail(String email);
}