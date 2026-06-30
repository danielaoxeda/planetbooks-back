package com.rodrigomv.planetbooksback.config;

import com.rodrigomv.planetbooksback.service.image.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuración para servir archivos estáticos (imágenes de productos).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ImageStorageService imageStorageService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadDir = imageStorageService.getUploadDir().toAbsolutePath().toString();
        
        if (!uploadDir.endsWith(File.separator)) {
            uploadDir += File.separator;
        }

        // En Windows, aseguramos que la ruta use slashes y tenga el prefijo file:///
        String location = "file:///" + uploadDir.replace("\\", "/");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
