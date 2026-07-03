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
                title = "h'our",
                version = "0.0.1",
                description = "h'our API 명세  "
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
                .pathsToMatch("/api/admin/products", "/api/products","/api/products/*","/api/admin/products/*")
                .group("Produce API")
                .build();
    }
    @Bean
    public GroupedOpenApi categoryApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/admin/categories", "/api/categories", "/api/categories/*","/api/admin/categories/*")
                .group("Category API")
                .build();
    }
    @Bean
    public GroupedOpenApi orderApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/admin/orders", "/api/orders", "/api/orders/*","/api/admin/orders/*")
                .group("Order API")
                .build();
    }
    @Bean
    public GroupedOpenApi cartApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/admin/carts", "/api/carts", "/api/carts/*","/api/admin/carts/*")
                .group("Cart API")
                .build();
    }
    @Bean
    public GroupedOpenApi paymentApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/admin/payments", "/api/payments","/api/payments/*","/api/admin/payments/*")
                .group("Payment API")
                .build();
    }
    @Bean
    public GroupedOpenApi reservationApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch(
                        "/api/admin/reservations", "/api/reservations", "/api/reservations/*","/api/admin/reservations/*",
                        "/api/admin/lessons", "/api/lessons", "/api/lessons/*" , "/api/admin/lessons/*"
                )
                .group("Reservation API")
                .build();
    }
}
