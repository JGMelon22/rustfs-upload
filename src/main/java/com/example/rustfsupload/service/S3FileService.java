package com.example.rustfsupload.service;

import io.awspring.cloud.s3.*;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class S3FileService {

    private final S3Template s3Template;

    @Value("${rustfs.bucket}")
    private String bucket;

    public S3FileService(S3Template s3Template) {
        this.s3Template = s3Template;
    }

    /**
     * Uploads a file and returns the generated object key.
     */
    public String upload(MultipartFile file) throws IOException {
        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // S3Template.upload() handles PutObjectRequest details internally
        s3Template.upload(bucket, key, file.getInputStream());

        return key;
    }

    /**
     * Downloads a file by key and returns it as an S3Resource.
     * S3Resource implements Spring's Resource interface, so it plugs
     * directly into ResponseEntity<Resource>.
     */
    public S3Resource download(String key) {
        return s3Template.download(bucket, key);
    }

    /**
     * List all object keys in the bucket.
     */
    public List<String> listFiles() {
        return s3Template.listObjects(bucket, "")
                .stream()
                .map(S3Resource::getFilename)
                .toList();
    }
}