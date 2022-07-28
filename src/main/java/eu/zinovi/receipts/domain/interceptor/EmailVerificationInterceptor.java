package eu.zinovi.receipts.domain.interceptor;

import eu.zinovi.receipts.service.UserService;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EmailVerificationInterceptor implements HandlerInterceptor {
    private final UserService userService;

    public EmailVerificationInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (request.getRequestURI() != null &&
                (request.getRequestURI().equals("/user/login") ||
                        request.getRequestURI().equals("/user/logout") ||
                        request.getRequestURI().equals("/error") ||
                        request.getRequestURI().startsWith("/js") ||
                        request.getRequestURI().startsWith("/css") ||
                        request.getRequestURI().startsWith("/image") ||
                        request.getRequestURI().startsWith("/user/verify/email")
                )) {
            return true;
        }
        if (request.getRemoteUser() == null) {
            return true;
        }

        if (userService.emailNotVerified(request.getRemoteUser())) {
            response.sendRedirect("/user/verify/email");
            return false;
        }
        return true;

    }
}
