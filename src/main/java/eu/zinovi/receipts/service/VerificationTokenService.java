package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.entity.User;

public interface VerificationTokenService {
    String createVerificationToken(String email);

    boolean verifyToken(String token);

    void deleteAllByUser(User user);
}
