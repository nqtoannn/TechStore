package com.quoctoan.shoestore.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class StorageService {
    public Cloudinary cloudinary() {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dppqnirq3",
                "api_key", "778844457533951",
                "api_secret", "Z3bbVAYV0aL0y5OB096Ei_vJKJw"));
        return cloudinary;
    }

    // Check file type: image png, jpg, jpeg, bmp
    public boolean isImage(MultipartFile file) {
        return Arrays.asList(new String[] {"image/png","image/jpg","image/jpeg","image/bmp"})
                .contains(Objects.requireNonNull(file.getContentType()).trim().toLowerCase());
    }

    // Upload image to cloudinary
    public String uploadImages(MultipartFile file, String namePath) {
        String imageUrl = "";
        try {
                String randomString = UUID.randomUUID().toString();
                String updatedNamePath = namePath + "/" + randomString;
                Map uploadResult = this.cloudinary().uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto", "public_id", updatedNamePath)
                );
                imageUrl = (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return imageUrl;
    }
}