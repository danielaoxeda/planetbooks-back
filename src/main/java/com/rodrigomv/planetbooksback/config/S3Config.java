package com.rodrigomv.planetbooksback.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@ConditionalOnProperty(name = "app.r2.enabled", havingValue = "true", matchIfMissing = true)
public class S3Config {

    @Value("${app.r2.endpoint:}")
    private String endpoint;

    @Value("${app.r2.region:auto}")
    private String region;

    @Value("${app.r2.access-key:}")
    private String accessKey;

    @Value("${app.r2.secret-key:}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        // app.r2.endpoint es el endpoint S3-compatible de R2 para escritura.
        // NO usar app.r2.public-url aquí: esa URL es solo para lectura (CDN pública).
        if (endpoint == null || endpoint.isBlank() || accessKey == null || accessKey.isBlank() || secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("R2 está habilitado pero falta configuración. Verificá: app.r2.endpoint, app.r2.access-key, app.r2.secret-key.");
        }
        String effectiveEndpoint = endpoint;
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
            .endpointOverride(URI.create(effectiveEndpoint))
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .forcePathStyle(true)
            .build();
    }
}