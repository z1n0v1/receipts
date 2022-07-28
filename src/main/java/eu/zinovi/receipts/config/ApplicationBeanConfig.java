package eu.zinovi.receipts.config;

import com.google.gson.*;
import eu.zinovi.receipts.util.LocalDateTimeAdapter;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDateTime;

@Configuration
public class ApplicationBeanConfig {

    @Bean
    public CacheManager cacheManager() {
        return new org.springframework.cache.concurrent.ConcurrentMapCacheManager();
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class,new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }
}
