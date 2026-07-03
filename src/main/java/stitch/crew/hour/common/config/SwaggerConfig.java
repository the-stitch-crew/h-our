package stitch.crew.hour.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "테크업 쇼핑몰",
                version = "0.0.1",
                description = "테크업 쇼핑몰 API 명세  "
        )
)
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "Bearer Authentication",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }

    @Bean
    public GroupedOpenApi authApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/auth/*")
                .group("Authentication API")
                .build();
    }
    @Bean
    public GroupedOpenApi userApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/users/*")
                .group("User API")
                .build();
    }
    @Bean
    public GroupedOpenApi productApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/products/*","/api/admin/products/*")
                .build();
    }
    @Bean
    public GroupedOpenApi categoryApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/categories/*","/api/admin/categories/*")
                .build();
    }
    @Bean
    public GroupedOpenApi orderApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/orders/*","/api/admin/orders/*")
                .build();
    }
    @Bean
    public GroupedOpenApi cartApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/carts/*","/api/admin/carts/*")
                .build();
    }
    @Bean
    public GroupedOpenApi paymentApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/payments/*","/api/admin/payments/*")
                .build();
    }
    @Bean
    public GroupedOpenApi reservationApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/reservations/*","/api/admin/reservations/*", "/api/lessons/*" , "/api/admin/lessons/*")
                .build();
    }
}
