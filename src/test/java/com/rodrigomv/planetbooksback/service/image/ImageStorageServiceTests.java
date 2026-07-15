package com.rodrigomv.planetbooksback.service.image;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageStorageServiceTests {

    private final Path base = Path.of("target/test-uploads");

    @AfterEach
    void cleanup() throws Exception {
        if (Files.exists(base)) {
            Files.walk(base)
                .map(Path::toFile)
                .sorted((a,b)->-a.compareTo(b))
                .forEach(File::delete);
        }
    }

    @Test
    void saveAndDeleteImage_shouldWork() throws Exception {
        ImageStorageService svc = new ImageStorageService(base.toString());

        byte[] content = "hello".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "img.png", "image/png", content);

        String returned = svc.saveImage(file, 7L);
        // verify file created under the configured upload dir
        Path productDir = svc.getUploadDir().resolve("products").resolve("7");
        assertThat(Files.exists(productDir)).isTrue();

        // find saved file name
        String filename = Files.list(productDir).findFirst().get().getFileName().toString();
        Path absolute = productDir.resolve(filename);
        assertThat(Files.exists(absolute)).isTrue();

        // delete using the path format expected by deleteImage (parent resolves + cleanPath)
        String rel = "/" + svc.getUploadDir().getFileName().toString() + "/products/7/" + filename;
        boolean deleted = svc.deleteImage(rel);
        assertTrue(deleted);
    }
}
