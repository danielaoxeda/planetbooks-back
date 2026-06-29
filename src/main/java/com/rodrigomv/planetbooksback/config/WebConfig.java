package com.rodrigomv.planetbooksback.config;

import com.rodrigomv.planetbooksback.service.image.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * Configuración para servir archivos estáticos (imágenes de productos).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ImageStorageService imageStorageService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = imageStorageService.getUploadDir().getParent();
        String uploadAbsolutePath = uploadDir.toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadAbsolutePath);
    }
}
