package com.springboot.boxo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
public class StorageService {
    private final AmazonS3 s3client;

    public StorageService(AmazonS3 s3client) {
        this.s3client = s3client;
    }


    public void uploadBase64ToS3(String bucketName, String base64Image, String fileName) {
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
        s3client.putObject(new PutObjectRequest(bucketName, fileName + "." + imageType, byteArrayInputStream, metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead)
                .withMetadata(metadata));
    }
}
