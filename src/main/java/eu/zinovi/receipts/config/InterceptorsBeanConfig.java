package eu.zinovi.receipts.config;

import eu.zinovi.receipts.domain.interceptor.EmailVerificationInterceptor;
import eu.zinovi.receipts.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorsBeanConfig implements WebMvcConfigurer {
    private final UserService userService;

    public InterceptorsBeanConfig(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(new EmailVerificationInterceptor(userService))
                .addPathPatterns("/**");
    }
}
