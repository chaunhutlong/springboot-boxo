package com.springboot.boxo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StorageService {
    private final AmazonS3 s3Client;

    @Value("${aws.bucket.name}")
    private String bucketName;

    public StorageService(AmazonS3 s3client) {
        this.s3Client = s3client;
    }

    public Map<String, String> uploadBase64ToS3(String base64Image, String fileName) {
        fileName = System.currentTimeMillis() + "-" + fileName;
        String[] strings = base64Image.split(",");
        String imageType = switch (strings[0]) { // check image's extension
            case "data:image/jpeg;base64" -> "jpeg";
            case "data:image/png;base64" -> "png";
            default -> // should write cases for more images types
                    "jpg";
        };

        // convert base64 string to binary data
        byte[] imageBytes = DatatypeConverter.parseBase64Binary(strings[1]);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType("image/" + imageType);

        PutObjectRequest request = new PutObjectRequest(bucketName, fileName + "." + imageType, byteArrayInputStream, metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead)
                .withMetadata(metadata);

       s3Client.putObject(request);

        // Generate and return the URL of the uploaded image
        Map<String, String> response = new HashMap<>();
        response.put("url", s3Client.getUrl(bucketName, fileName + "." + imageType).toString());
        response.put("key", fileName + "." + imageType);

        return response;
    }

    public Map<String, String> uploadFileToS3(MultipartFile file, String fileName) {
        try {
            fileName = System.currentTimeMillis() + "_" + fileName;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            InputStream inputStream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            s3Client.putObject(putObjectRequest);

            Map<String, String> response = new HashMap<>();
            response.put("url", s3Client.getUrl(bucketName, fileName).toString());
            response.put("key", fileName);

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }



    public void deleteFileFromS3(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
    }

    public void deleteImagesFromS3(List<String> keys) {
        keys.forEach(key -> s3Client.deleteObject(bucketName, key));
    }
}
