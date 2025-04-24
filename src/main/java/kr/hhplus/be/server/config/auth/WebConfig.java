package kr.hhplus.be.server.config.auth;

import kr.hhplus.be.server.common.auth.AuthUserArgumentResolver;
import kr.hhplus.be.server.common.support.ErrorHandlingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthUserArgumentResolver authUserArgumentResolver;
    private final ErrorHandlingInterceptor errorHandlingInterceptor;

    public WebConfig(AuthUserArgumentResolver authUserArgumentResolver, ErrorHandlingInterceptor errorHandlingInterceptor) {
        this.authUserArgumentResolver = authUserArgumentResolver;
        this.errorHandlingInterceptor = errorHandlingInterceptor;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authUserArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(errorHandlingInterceptor).addPathPatterns("/**");
    }
}
