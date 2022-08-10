package eu.zinovi.receipts.config;

import eu.zinovi.receipts.domain.interceptor.EmailVerificationInterceptor;
import eu.zinovi.receipts.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorsBeanConfig implements WebMvcConfigurer {
    private final UserServiceImpl userServiceImpl;

    public InterceptorsBeanConfig(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(new EmailVerificationInterceptor(userServiceImpl))
                .addPathPatterns("/**");
    }
}
