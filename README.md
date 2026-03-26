# rustfs-upload

A simple Spring Boot 3 REST API to upload, download, and list files using a [RustFS](https://rustfs.com) bucket (S3-compatible).

## Tech Stack

- Java 21
- Spring Boot 3.5.12
- Spring Cloud AWS 4.0.0 (`S3Template`)
- RustFS (S3-compatible object storage)
- Springdoc OpenAPI (Scalar support)
- Scalar UI (`scalar-webmvc`) package

## Prerequisites

- Java 21+
- Maven
- A running RustFS instance

## Configuration

Edit `src/main/resources/application.yml` with your RustFS credentials:

```yaml
spring:
  application:
    name: rustfs-upload
  threads:
    virtual:
      enabled: true
  cloud:
    aws:
      credentials:
        access-key: your-secret-key
        secret-key: your-secret-key
      region:
        static: us-east-1
      s3:
        endpoint: http://localhost:9000  
        path-style-access-enabled: true 
  servlet:
    multipart:
      max-file-size: 100MB # How much you allow multipart size
      max-request-size: 100MB

scalar:
  enabled: true
  url: /v3/api-docs

rustfs:
  bucket: my-bucket Your bucket name
```

## Running

```bash
mvn spring-boot:run
```

## API Endpoints

| Method | Path                    | Description              |
|--------|-------------------------|--------------------------|
| POST   | `/api/files/upload`     | Upload a file            |
| GET    | `/api/files`            | List all files in bucket |
| GET    | `/api/files/{key}`      | Download a file by key   |
| DELETE | `/api/files/{key}`      | Deletes a file by key    |

## Scalar UI

Available at: [http://localhost:8080/scalar](http://localhost:8080/scalar)

## References

- [Spring Cloud AWS - S3 Docs](https://docs.awspring.io/spring-cloud-aws/docs/3.3.0/reference/html/index.html#spring-cloud-aws-s3)
- [Springdoc OpenAPI](https://springdoc.org/)
- [Scalar UI - API Reference for Java](https://scalar.com/products/api-references/integrations/java)
- [Spring Cloud AWS Project](https://spring.io/projects/spring-cloud-aws)

## Usage Examples

```bash
# Upload a file
curl -X POST http://localhost:8080/api/files/upload \
  -F "file=@photo.jpg"

# List files
curl http://localhost:8080/api/files

# Download a file (use the key returned from upload)
curl -OJ http://localhost:8080/api/files/uuid_photo.jpg

# Delete a file (use the key returned from upload)
curl -X DELETE http://localhost:8080/api/files/uuid_photo.jpg
```
