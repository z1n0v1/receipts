package eu.zinovi.receipts.service;

public interface EmailVerificationService {
    void sendVerificationEmail(String email);
}
