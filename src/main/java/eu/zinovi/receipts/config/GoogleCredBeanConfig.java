package eu.zinovi.receipts.config;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GoogleCredBeanConfig {

    @Value("${GOOGLE_CREDENTIALS}")
    private String gservicesConfig;

    @Bean
    public Storage storage() throws IOException {
        Credentials credentials = credentials();
        return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }

    @Bean
    public ImageAnnotatorClient imageAnnotatorClient() throws IOException {

        ImageAnnotatorSettings imageAnnotatorSettings =
                ImageAnnotatorSettings.newBuilder()
                        .setCredentialsProvider(FixedCredentialsProvider.create(credentials()))
                        .build();

        return ImageAnnotatorClient.create(imageAnnotatorSettings);
    }

    public Credentials credentials() throws IOException {

        JSONObject jsonObject = new JSONObject(gservicesConfig);
        InputStream stream = new ByteArrayInputStream(jsonObject.toString().getBytes());

        return GoogleCredentials.fromStream(stream);
    }
}
