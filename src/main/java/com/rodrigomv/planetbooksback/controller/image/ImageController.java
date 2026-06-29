package com.rodrigomv.planetbooksback.controller.image;

import com.rodrigomv.planetbooksback.exception.BadRequestException;
import com.rodrigomv.planetbooksback.exception.ResourceNotFoundException;
import com.rodrigomv.planetbooksback.model.dto.ImageResponseDTO;
import com.rodrigomv.planetbooksback.model.entity.Product;
import com.rodrigomv.planetbooksback.repository.ProductRepository;
import com.rodrigomv.planetbooksback.service.image.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para gestionar la subida de imágenes de productos.
 */
@RestController
@RequestMapping("/api/v1/products/{productId}/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageStorageService imageStorageService;
    private final ProductRepository productRepository;

    /**
     * Sube una imagen principal para el producto.
     * Reemplaza la imagen principal existente si hay una.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImageResponseDTO> uploadMainImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productId));

        // Elimina la imagen anterior si existe
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            imageStorageService.deleteImage(product.getImage());
        }

        String imageUrl = imageStorageService.saveImage(file, productId);
        product.setImage(imageUrl);
        productRepository.save(product);

        return ResponseEntity.ok(buildResponse(imageUrl, file));
    }

    /**
     * Añade una imagen a la galería del producto.
     */
    @PostMapping(value = "/gallery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImageResponseDTO> uploadGalleryImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productId));

        String imageUrl = imageStorageService.saveImage(file, productId);

        if (product.getGallery() == null) {
            product.setGallery(new ArrayList<>());
        }
        product.getGallery().add(imageUrl);
        productRepository.save(product);

        return ResponseEntity.ok(buildResponse(imageUrl, file));
    }

    /**
     * Elimina una imagen de la galería.
     */
    @DeleteMapping("/gallery")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGalleryImage(
            @PathVariable Long productId,
            @RequestParam String imageUrl) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productId));

        if (product.getGallery() != null && product.getGallery().remove(imageUrl)) {
            imageStorageService.deleteImage(imageUrl);
            productRepository.save(product);
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * Sube múltiples imágenes de una vez.
     * La primera imagen se establece como imagen principal,
     * las demás van a la galería.
     */
    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ImageResponseDTO>> uploadMultipleImages(
            @PathVariable Long productId,
            @RequestParam("files") List<MultipartFile> files) {

        if (files == null || files.isEmpty()) {
            throw new BadRequestException("No se proporcionaron archivos");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productId));

        if (product.getGallery() == null) {
            product.setGallery(new ArrayList<>());
        }

        List<ImageResponseDTO> responses = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String imageUrl = imageStorageService.saveImage(file, productId);

            if (i == 0) {
                // Primera imagen como imagen principal
                if (product.getImage() != null) {
                    imageStorageService.deleteImage(product.getImage());
                }
                product.setImage(imageUrl);
            } else {
                // Resto a la galería
                product.getGallery().add(imageUrl);
            }

            responses.add(buildResponse(imageUrl, file));
        }

        productRepository.save(product);

        return ResponseEntity.ok(responses);
    }

    private ImageResponseDTO buildResponse(String imageUrl, MultipartFile file) {
        return ImageResponseDTO.builder()
                .url(imageUrl)
                .filename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .build();
    }
}
