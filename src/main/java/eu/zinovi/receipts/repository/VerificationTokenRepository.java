package eu.zinovi.receipts.repository;

import eu.zinovi.receipts.domain.model.entity.User;
import eu.zinovi.receipts.domain.model.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    Optional<VerificationToken> getByUserEmail(String email);

    Optional<VerificationToken> getByToken(String token);

    void deleteAllByUser(User user);
}