package com.example.ecommerce.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@Slf4j
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket}")
    private String bucketName;

    private MinioClient minioClient;

    @Bean
    public MinioClient minioClient() {
        // Csak a klienst hozza létre (nincs hálózati hívás), így ez a bean sosem hiúsul meg
        // amiatt, hogy a MinIO még nem elérhető az app indulásakor.
        minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        return minioClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ensureBucketExists() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket létrehozva: {}", bucketName);
            } else {
                log.info("Bucket már létezik: {}", bucketName);
            }
        } catch (Exception e) {
            log.warn("Nem sikerült elérni a MinIO-t ({}) vagy létrehozni/ellenőrizni a(z) '{}' bucketet. " +
                    "A képfeltöltés/letöltés nem fog működni, amíg a MinIO nem elérhető. Ok: {}",
                    endpoint, bucketName, e.getMessage());
        }
    }
}
