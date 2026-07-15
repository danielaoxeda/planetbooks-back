package com.rodrigomv.planetbooksback.service.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para almacenar y gestionar imágenes de productos en Cloudflare R2.
 * Usa el SDK de AWS S3 compatible con la API de R2.
 */
@Service
public class ImageStorageService {

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/webp"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Value("${app.r2.enabled:true}")
    private boolean r2Enabled;

    @Value("${app.r2.bucket:planetbooks-images}")
    private String bucket;

    @Value("${app.r2.public-url:}")
    private String publicUrl;

    @Value("${app.r2.endpoint:}")
    private String endpoint;

    @Value("${app.r2.region:auto}")
    private String region;

    @Value("${app.r2.access-key:}")
    private String accessKey;

    @Value("${app.r2.secret-key:}")
    private String secretKey;

    private S3Client s3Client;

    @PostConstruct
    public void init() {
        if (!r2Enabled) {
            return;
        }
        if (endpoint == null || endpoint.isBlank() || accessKey == null || accessKey.isBlank()
                || secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException(
                "R2 está habilitado pero falta configuración. Verificá: app.r2.endpoint, " +
                "app.r2.access-key, app.r2.secret-key. Aktivá el perfil 'prod' o configurá las variables de entorno.");
        }

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .forcePathStyle(true) // R2 requiere path-style
                .build();

        ensureBucketExists();
    }

    private void ensureBucketExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        } catch (BucketAlreadyOwnedByYouException e) {
            // Bucket ya existe y es nuestro, seguir
        }
    }

    /**
     * Sube una imagen a R2 y retorna la URL pública.
     *
     * @param file      El archivo a guardar
     * @param productId El ID del producto (para organizar en el path)
     * @return La URL pública de la imagen
     */
    public String saveImage(MultipartFile file, Long productId) {
        validateFile(file);
        ensureR2Ready();

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + extension;

        String key = "products/" + productId + "/" + newFilename;

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build(),
            RequestBody.fromBytes(file.getBytes())
        );

        return buildPublicUrl(key);
    }

    /**
     * Elimina una imagen de R2.
     *
     * @param imageUrl La URL pública de la imagen
     * @return true si se eliminó correctamente
     */
    public boolean deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }

        String key = extractKeyFromUrl(imageUrl);
        if (key == null) {
            return false;
        }

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Resuelve la URL pública de una imagen. Maneja tanto URLs nuevas de R2
     * como rutas legacy del filesystem local (/uploads/products/...).
     *
     * @param imagePath La URL o ruta de la imagen
     * @return La URL pública completa, o la original si no se puede resolver
     */
    public String resolveImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return imagePath;
        }
        // Ya es una URL pública de R2
        if (publicUrl != null && !publicUrl.isBlank() && imagePath.startsWith(publicUrl)) {
            return imagePath;
        }
        // Legacy local path: /uploads/products/X/file.jpg → R2 URL
        if (imagePath.startsWith("/uploads/products/") || imagePath.startsWith("uploads/products/")) {
            String key = imagePath.replaceFirst("^/?uploads/", "");
            return buildPublicUrl(key);
        }
        // Otra ruta relativa: products/X/file.jpg
        if (imagePath.startsWith("products/")) {
            return buildPublicUrl(imagePath);
        }
        return imagePath;
    }

    /**
     * Obtiene la URL pública de una imagen dado su key.
     *
     * @param relativePath La ruta relativa (ej: products/123/abc.jpg)
     * @return La URL pública completa
     */
    public String getPublicUrl(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }
        return buildPublicUrl(relativePath);
    }

    private String buildPublicUrl(String key) {
        String base = publicUrl.endsWith("/") ? publicUrl.substring(0, publicUrl.length() - 1) : publicUrl;
        String cleanKey = key.startsWith("/") ? key.substring(1) : key;
        return base + "/" + cleanKey;
    }

    private void ensureR2Ready() {
        if (s3Client == null) {
            throw new IllegalStateException(
                "R2 no está configurado. Aktivá el perfil 'prod' (SPRING_PROFILES_ACTIVE=prod) " +
                "o configurá las variables de entorno APP_R2_ENABLED, APP_R2_ENDPOINT, " +
                "APP_R2_ACCESS_KEY, APP_R2_SECRET_KEY.");
        }
    }

    private String extractKeyFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        if (publicUrl != null && !publicUrl.isEmpty() && imageUrl.startsWith(publicUrl)) {
            return imageUrl.substring(publicUrl.length()).replaceFirst("^/", "");
        }
        String clean = imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl;
        if (clean.startsWith("uploads/products/")) {
            return clean.substring("uploads/".length());
        }
        return clean;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo de 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Tipo de archivo no permitido. Solo se aceptan: JPEG, PNG, GIF, WebP");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }

    public String getBucket() {
        return bucket;
    }

    public boolean isR2Enabled() {
        return r2Enabled && s3Client != null;
    }
}
