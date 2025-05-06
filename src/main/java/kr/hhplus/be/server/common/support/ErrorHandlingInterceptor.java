package kr.hhplus.be.server.common.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ErrorHandlingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {

        if (ex != null) {
            logger.error("Unhandled exception occurred: [{}] {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        }
    }

}
