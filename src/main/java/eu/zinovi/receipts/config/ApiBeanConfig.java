package eu.zinovi.receipts.config;


import eu.zinovi.receipts.util.CloudStorage;
import eu.zinovi.receipts.util.ReceiptProcessApi;
import eu.zinovi.receipts.util.RegisterBGApi;
import eu.zinovi.receipts.util.impl.GoogleCloudStorage;
import eu.zinovi.receipts.util.impl.GoogleReceiptProcessApi;
import eu.zinovi.receipts.util.impl.RegisterBGApiImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiBeanConfig {
    @Value("${receipts.google.gcp.credentials.encoded-key}")
    private String googleCreds;

    @Value("${receipts.google.storage.bucket}")
    private String bucket;

    @Bean
    public ReceiptProcessApi receiptProcessApi() {
        return new GoogleReceiptProcessApi(googleCreds, bucket);
    }

    @Bean
    public CloudStorage cloudStorage() {
        return new GoogleCloudStorage(googleCreds, bucket);
    }

    @Bean
    public RegisterBGApi registerBGApi() {
        return new RegisterBGApiImpl();
    }
}
