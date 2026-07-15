package com.rodrigomv.planetbooksback.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración web.
 * Las imágenes ahora se sirven desde Cloudflare R2 directamente — no se necesitan
 * resource handlers para /uploads/ ya que se almacenan en el CDN.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Nada que hacer aquí: R2 sirve las imágenes por su URL pública.
    // Si en el futuro necesitas un proxy de imágenes (resize, watermark, etc.),
    // ese sería el lugar para agregarlo.
}
