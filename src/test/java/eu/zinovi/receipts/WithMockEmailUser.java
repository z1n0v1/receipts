package eu.zinovi.receipts;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockEmailUser {
    String email() default "demo@demo";
    String password() default "test";
    String displayName() default "displaytest";
    String firstName() default "firstName";
    String lastName() default "lastName";
    String picture() default "picture";
    boolean emailVerified() default true;
    boolean enabled() default true;
    String[] roles() default { "USER" };
}
