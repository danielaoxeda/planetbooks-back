package com.rodrigomv.planetbooksback.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI planetbooksOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
            .components(new Components().addSecuritySchemes(securitySchemeName,
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT Token")))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .info(new Info()
                .title("PlanetBooks API")
                .version("1.0.0")
                .description("API REST para gestión de usuarios, productos, pedidos y carrito con autenticación JWT.")
                .contact(new Contact()
                    .name("PlanetBooks")
                    .url("http://localhost:8080")));
    }
}
