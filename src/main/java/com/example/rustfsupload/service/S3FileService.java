package com.example.rustfsupload.service;

import io.awspring.cloud.s3.*;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
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
    public String upload(InputStream stream, String fileName, String contentType) {
        String key = UUID.randomUUID() + "_" + fileName;

        ObjectMetadata metadata = ObjectMetadata.builder()
                .contentType(contentType)
                .build();

        // S3Template.upload() handles PutObjectRequest details internally
        s3Template.upload(bucket, key, stream, metadata);

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
     * Removes the object with the given key from the bucket.
     * No-op if the object does not exist.
     */
    public void remove(String key) {
        if (s3Template.objectExists(bucket, key)) {
            s3Template.deleteObject(bucket, key);
        }
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