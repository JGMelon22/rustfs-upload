package com.example.rustfsupload.controller;

import com.example.rustfsupload.service.S3FileService;
import io.awspring.cloud.s3.S3Resource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final S3FileService fileService;

    public FileController(S3FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * POST /api/files/upload
     * Body: multipart/form-data, field name "file"
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> upload(
            @RequestPart("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "File must not be empty."));
        }

        String key = fileService.upload(file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType());

        return ResponseEntity.ok(Map.of(
                "message", "File uploaded successfully.",
                "key", key,
                "originalFilename", Objects.requireNonNull(file.getOriginalFilename())
        ));
    }

    /**
     * GET /api/files
     * Returns a list of all object keys in the bucket.
     */
    @GetMapping
    public ResponseEntity<List<String>> listFiles() {
        return ResponseEntity.ok(fileService.listFiles());
    }

    /**
     * GET /api/files/{key}
     * Returns the raw file bytes with its original content type,
     * triggering a download in the browser/client.
     * <p>
     * The key must be URL-encoded if it contains special characters.
     */
    @GetMapping("/{key}")
    public ResponseEntity<Resource> download(@PathVariable String key) {
        S3Resource resource = fileService.download(key);

        // S3Resource exposes the object's metadata, including content type
        resource.contentType();
        String contentType = resource.contentType();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    /**
     * DELETE /api/files/{key}
     * Removes the object with the given key from the bucket.
     * <p>
     * Returns 204 No Content on success.
     * The key must be URL-encoded if it contains special characters.
     */
    @DeleteMapping("/{key}")
    public ResponseEntity<Void> remove(@PathVariable String key) throws IOException {
        fileService.remove(key);
        return ResponseEntity.noContent().build();
    }
}