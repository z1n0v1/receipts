package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.entity.User;
import eu.zinovi.receipts.domain.model.entity.VerificationToken;
import eu.zinovi.receipts.repository.UserRepository;
import eu.zinovi.receipts.repository.VerificationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificationTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerificationTokenService.class);
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    private final CacheManager cacheManager;

    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository, UserRepository userRepository, CacheManager cacheManager) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
    }

    public String createVerificationToken(String email) {
        VerificationToken verificationToken = verificationTokenRepository.getByUserEmail(email).orElse(null);
        if (verificationToken == null) {
            verificationToken = new VerificationToken();
        }
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setExpiryDate(LocalDateTime.now().plusDays(1));

        User user = userRepository.getUserByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("User with email " + email + " not found");
        }
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);
        return verificationToken.getToken();
    }

    public boolean verifyToken(String token) {
        if (token == null) {
            return false;
        }
        VerificationToken verificationToken = verificationTokenRepository.getByToken(token).orElse(null);
        if (verificationToken == null) {
            return false;
        }
        if (verificationToken.getToken().equals(token)) {
            if (verificationToken.getExpiryDate().isAfter(LocalDateTime.now())) {

                User user = verificationToken.getUser();
                try {
                cacheManager.getCache("emailVerification").evict(user.getEmail());
                } catch (NullPointerException e) {
                    LOGGER.error("Error evicting cache for email {}", user.getEmail());
                }
                user.setEmailLoginDisabled(false);
                user.setEmailVerified(true);
                userRepository.save(user);

                verificationTokenRepository.delete(verificationToken);
                return true;
            }
        }
        verificationTokenRepository.delete(verificationToken);
        return false;
    }

    public void deleteAllByUser(User user) {
        verificationTokenRepository.deleteAllByUser(user);
    }
}
