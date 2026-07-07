package stitch.crew.hour.image.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.util.PreConditions;

import java.util.Set;

@Component
public class ImageValidate {
    private static final long MAX_FILE_SIZE_BYTES = 20 * 1024 * 1024L;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "png",
            "jpg",
            "jpeg"
    );

    public void validate(MultipartFile file){
        PreConditions.check(file == null || file.isEmpty(),  ErrorCode.FILE_EMPTY);
        PreConditions.check(file.getSize() > MAX_FILE_SIZE_BYTES,  ErrorCode.SIZE_INVALID);

        String extension = extractExtension(file.getOriginalFilename());
        PreConditions.check(!ALLOWED_EXTENSIONS.contains(extension),  ErrorCode.EXTENSIONS_INVALID);
    }

    public String extractExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BusinessException(ErrorCode.FILE_NAME_EMPTY);
        }

        int dotIndex = originalFilename.lastIndexOf(".");

        if (dotIndex == -1 || dotIndex == originalFilename.length() - 1) {
            throw new BusinessException(ErrorCode.EXTENSIONS_INVALID);
        }

        return originalFilename.substring(dotIndex + 1).toLowerCase();
    }
}
