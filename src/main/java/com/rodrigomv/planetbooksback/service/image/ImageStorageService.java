package com.rodrigomv.planetbooksback.service.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para almacenar y gestionar imágenes de productos.
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

    private final Path uploadDir;

    public ImageStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
            Files.createDirectories(this.uploadDir.resolve("products"));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de uploads", e);
        }
    }

    /**
     * Guarda una imagen y retorna la ruta relativa.
     *
     * @param file El archivo a guardar
     * @param productId El ID del producto (para organizar en subcarpetas)
     * @return La ruta relativa del archivo guardado
     */
    public String saveImage(MultipartFile file, Long productId) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + extension;

        Path productDir = uploadDir.resolve("products").resolve(productId.toString());
        try {
            Files.createDirectories(productDir);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio del producto", e);
        }

        Path targetLocation = productDir.resolve(newFilename);
        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen", e);
        }

        // Retorna la ruta relativa: /uploads/products/{productId}/{filename}
        return "/uploads/products/" + productId + "/" + newFilename;
    }

    /**
     * Elimina una imagen del almacenamiento.
     *
     * @param imagePath La ruta relativa de la imagen
     * @return true si se eliminó correctamente, false si no existía
     */
    public boolean deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return false;
        }

        // Limpia la ruta y construye el path absoluto
        String cleanPath = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;
        Path filePath = uploadDir.getParent().resolve(cleanPath).normalize();

        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Obtiene la ruta absoluta de una imagen.
     *
     * @param relativePath La ruta relativa de la imagen
     * @return La ruta absoluta
     */
    public Path getAbsolutePath(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }
        String cleanPath = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        return uploadDir.getParent().resolve(cleanPath).normalize();
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
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
        String extension = filename.substring(filename.lastIndexOf("."));
        // Normaliza la extensión a minúsculas
        return extension.toLowerCase();
    }

    /**
     * Retorna la ruta base del directorio de uploads para configuración de recursos estáticos.
     */
    public Path getUploadDir() {
        return uploadDir;
    }
}
