package stitch.crew.hour.image.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import stitch.crew.hour.image.domain.Image;
import stitch.crew.hour.image.domain.ThumbnailDomain;
import stitch.crew.hour.image.repository.ImageRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final S3StorageService s3Service;
    private final ImageValidate imageValidate;

    @Transactional
    public String saveThumbnail(ThumbnailDomain domain, Long domainId, MultipartFile file) {
        imageValidate.validate(file);
        String s3key = generateThumbnailKey(domain, domainId, imageValidate.extractExtension(file.getOriginalFilename()));
        s3Service.upload(file, s3key);
        //보상 트랜잭션
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    try {
                        s3Service.delete(s3key);
                    } catch (Exception e) {
                        log.error("S3 cleanup failed after rollback: {}", e.getMessage());
                    }
                }

            }
        });
        return s3key;
    }

    @Transactional
    public void saveProduct(Long productId, List<MultipartFile> files) {
        List<String> cUrls = new ArrayList<>();
        files.forEach(file -> imageValidate.validate(file));
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String s3key = generateProductKey(productId, imageValidate.extractExtension(file.getOriginalFilename()));
            s3Service.upload(file, s3key);
            Image image = new Image(s3key, i+1);
            cUrls.add(s3key);
            imageRepository.save(image);
        }
        //보상 트랜잭션
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    try {
                        s3Service.deleteImages(cUrls);
                    } catch (Exception e) {
                        log.error("S3 cleanup failed after rollback: {}", e.getMessage());
                    }
                }

            }
        });
    }

    public String getPresignedUrl(String s3key) {
        return s3Service.createShowPresignedUrl(s3key);
    }

    public void deleteThumbnail(String s3Key) {
        s3Service.delete(s3Key);
    }
    private String generateThumbnailKey(ThumbnailDomain domain, Long domainId, String extension) {
        return "thumbnail/%s/%d/%s/%s.%s".formatted(domain, domainId, LocalDate.now(), UUID.randomUUID(), extension);
    }

    private String generateProductKey(Long productId, String extension) {
        return "product/%d/%s/%s.%s".formatted(productId, LocalDate.now(), UUID.randomUUID(), extension);
    }
}
