package com.example.airbnb.airbnb_app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Airbnb API", version = "1.0"),
        tags = {
                @Tag(name = "Authentication"),
                @Tag(name = "Browse Hotels"),
                @Tag(name = "Booking Flow"),
                @Tag(name = "Booking Guests"),
                @Tag(name = "Admin Hotel"),
                @Tag(name = "Admin Hotel-Room"),
                @Tag(name = "Admin Inventory"),
                @Tag(name = "User Profile"),
                @Tag(name = "Razorpay Payment"),
                @Tag(name = "Super Admin"),
                @Tag(name = "Admin Reports"),
        }
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("JWT Token"))
                .components(new Components().addSecuritySchemes("JWT Token", new SecurityScheme()
                        .name("JWT Token").type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));

    }
}
