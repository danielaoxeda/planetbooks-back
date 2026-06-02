package com.rodrigomv.planetbooksback.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.api.public-url:}")
    private String publicApiUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
            .info(new Info()
                .title("PlanetBooks API")
                .version("1.0.0")
                .description("API documentation for PlanetBooks - Online Book Store")
                .contact(new Contact()
                    .name("PlanetBooks")
                    .url("https://planetbooks.com")))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Enter JWT token")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

        if (StringUtils.hasText(publicApiUrl)) {
            openAPI.setServers(List.of(new Server().url(publicApiUrl).description("API pública")));
        }

        return openAPI;
    }
}
