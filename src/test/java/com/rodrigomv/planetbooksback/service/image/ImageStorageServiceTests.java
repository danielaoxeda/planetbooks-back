package com.rodrigomv.planetbooksback.service.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageStorageServiceTests {

    @Mock
    private S3Client s3Client;

    private ImageStorageService svc;

    @BeforeEach
    void setUp() {
        svc = new ImageStorageService();

        ReflectionTestUtils.setField(svc, "r2Enabled", true);
        ReflectionTestUtils.setField(svc, "bucket", "planetbooks-images");
        ReflectionTestUtils.setField(svc, "publicUrl", "https://pub-cfaf1234abcd.r2.dev");
        ReflectionTestUtils.setField(svc, "endpoint", "https://7ecc67b065f334d4f8c41e0959e33401.r2.cloudflarestorage.com");
        ReflectionTestUtils.setField(svc, "region", "auto");
        ReflectionTestUtils.setField(svc, "accessKey", "test-key");
        ReflectionTestUtils.setField(svc, "secretKey", "test-secret");
        ReflectionTestUtils.setField(svc, "s3Client", s3Client);
    }

    @Test
    void saveImage_shouldUploadToR2AndReturnPublicUrl() {
        byte[] content = "fake image".getBytes();
        MockMultipartFile file = new MockMultipartFile(
            "file", "photo.png", "image/png", content);

        String result = svc.saveImage(file, 42L);

        assertThat(result).startsWith("https://pub-cfaf1234abcd.r2.dev/");
        assertThat(result).contains("products/42/");
        assertThat(result).endsWith(".png");

        ArgumentCaptor<PutObjectRequest> reqCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(reqCaptor.capture(), any(RequestBody.class));

        PutObjectRequest captured = reqCaptor.getValue();
        assertThat(captured.bucket()).isEqualTo("planetbooks-images");
        assertThat(captured.key()).startsWith("products/42/");
        assertThat(captured.contentType()).isEqualTo("image/png");
    }

    @Test
    void saveImage_shouldRejectEmptyFile() {
        MockMultipartFile empty = new MockMultipartFile(
            "file", "empty.png", "image/png", new byte[0]);

        assertThatThrownBy(() -> svc.saveImage(empty, 1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("vacío");
    }

    @Test
    void saveImage_shouldRejectOversizedFile() {
        byte[] big = new byte[6 * 1024 * 1024]; // 6MB
        MockMultipartFile large = new MockMultipartFile(
            "file", "large.png", "image/png", big);

        assertThatThrownBy(() -> svc.saveImage(large, 1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("5MB");
    }

    @Test
    void saveImage_shouldRejectInvalidContentType() {
        MockMultipartFile bad = new MockMultipartFile(
            "file", "doc.pdf", "application/pdf", "data".getBytes());

        assertThatThrownBy(() -> svc.saveImage(bad, 1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Tipo de archivo no permitido");
    }

    @Test
    void deleteImage_shouldDeleteFromR2AndReturnTrue() {
        String url = "https://pub-cfaf1234abcd.r2.dev/products/5/abc.jpg";
        boolean result = svc.deleteImage(url);

        assertThat(result).isTrue();
        verify(s3Client).deleteObject(DeleteObjectRequest.builder()
            .bucket("planetbooks-images")
            .key("products/5/abc.jpg")
            .build());
    }

    @Test
    void deleteImage_shouldReturnFalseForNullOrEmptyUrl() {
        assertThat(svc.deleteImage(null)).isFalse();
        assertThat(svc.deleteImage("")).isFalse();
        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteImage_shouldReturnTrueEvenIfKeyNotFound() {
        String url = "https://pub-cfaf1234abcd.r2.dev/products/999/nope.jpg";
        boolean result = svc.deleteImage(url);

        assertThat(result).isTrue(); // el método retorna true tras intentar delete
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void getPublicUrl_shouldReturnCorrectUrl() {
        String result = svc.getPublicUrl("products/7/image.webp");
        assertThat(result).isEqualTo("https://pub-cfaf1234abcd.r2.dev/products/7/image.webp");
    }

    @Test
    void getPublicUrl_shouldHandleLeadingSlash() {
        String result = svc.getPublicUrl("/products/7/image.webp");
        assertThat(result).isEqualTo("https://pub-cfaf1234abcd.r2.dev/products/7/image.webp");
    }

    @Test
    void getPublicUrl_shouldReturnNullForNullInput() {
        assertThat(svc.getPublicUrl(null)).isNull();
    }

    @Test
    void getBucket_shouldReturnConfiguredBucket() {
        assertThat(svc.getBucket()).isEqualTo("planetbooks-images");
    }
}
