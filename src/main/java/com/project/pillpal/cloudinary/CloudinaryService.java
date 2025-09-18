package com.project.pillpal.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> uploadFile(MultipartFile file) throws IOException {
        return (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
    }

    public void deleteFile(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public Map<String, Object> uploadMedicationImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> uploadOptions = (Map<String, Object>) ObjectUtils.asMap(
                "folder", "pillpal/medications",
                "resource_type", "image",
                "transformation", "w_800,h_600,c_limit,q_auto,f_auto");

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(), uploadOptions);
        return result;
    }

    public void deleteMedicationImage(String publicId) throws IOException {
        if (publicId == null || publicId.trim().isEmpty()) {
            throw new IllegalArgumentException("Public ID cannot be empty");
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) cloudinary.uploader().destroy(publicId,
                    ObjectUtils.emptyMap());

            String resultStatus = (String) result.get("result");
            if (!"ok".equals(resultStatus)) {
                log.warn("Warning: deletion result - {}", resultStatus);
            }
        } catch (Exception e) {
            log.error("Error deleting image from Cloudinary: {}", e.getMessage());
            throw e;
        }
    }

    public String getImageUrl(String publicId) {
        if (publicId == null || publicId.trim().isEmpty()) {
            return null;
        }
        return cloudinary.url().generate(publicId);
    }

    public String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }

        try {
            int uploadIndex = imageUrl.indexOf("/upload/");
            if (uploadIndex == -1) {
                return null;
            }

            String pathAfterUpload = imageUrl.substring(uploadIndex + 8);

            if (pathAfterUpload.startsWith("v") && pathAfterUpload.indexOf("/") > 0) {
                int versionEnd = pathAfterUpload.indexOf("/");
                pathAfterUpload = pathAfterUpload.substring(versionEnd + 1);
            }

            int dotIndex = pathAfterUpload.lastIndexOf('.');
            if (dotIndex > 0) {
                return pathAfterUpload.substring(0, dotIndex);
            } else {
                return pathAfterUpload;
            }
        } catch (Exception e) {
            log.error("Error extracting publicId: {}", e.getMessage());
        }

        return null;
    }
}
