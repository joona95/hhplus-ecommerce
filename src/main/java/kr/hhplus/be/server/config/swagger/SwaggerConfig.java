package kr.hhplus.be.server.config.swagger;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi commerceGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("E-Commerce")
                .pathsToMatch("/api/v1/**")
                .addOpenApiCustomizer(openApi -> openApi.setInfo(new Info()
                        .title("E-Commerce API")
                        .description("E-Commerce API")
                        .version("1.0.0")))
                .build();
    }
}
