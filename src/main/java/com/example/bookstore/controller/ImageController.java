package com.example.bookstore.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            // First try to load from uploads folder
            Path uploadsPath = Paths.get("uploads/" + filename);
            if (Files.exists(uploadsPath)) {
                byte[] imageBytes = Files.readAllBytes(uploadsPath);
                ByteArrayResource resource = new ByteArrayResource(imageBytes);
                String contentType = getContentType(filename);
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            }
            
            // Fallback to static resources
            ClassPathResource resource = new ClassPathResource("static/img/" + filename);
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = resource.getInputStream().readAllBytes();
            ByteArrayResource byteResource = new ByteArrayResource(imageBytes);
            String contentType = getContentType(filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(byteResource);

        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> getUploadedImage(@PathVariable String filename) {
        // This is now redundant since main endpoint handles uploads first
        return getImage(filename);
    }

    @GetMapping("/test-image")
    public ResponseEntity<String> testImageAccess() {
        try {
            // First check uploads folder
            Path uploadsPath = Paths.get("uploads/tuduynhanhvacham.png");
            if (Files.exists(uploadsPath)) {
                return ResponseEntity.ok("Image found in uploads folder. Accessible at: /api/images/tuduynhanhvacham.png");
            }
            
            // Then check static resources
            ClassPathResource resource = new ClassPathResource("static/img/tuduynhanhvacham.png");
            if (resource.exists()) {
                return ResponseEntity.ok("Image found in static resources. Accessible at: /api/images/tuduynhanhvacham.png");
            }
            
            return ResponseEntity.ok("Image not found in either uploads/ or static/img/ folders");
        } catch (Exception e) {
            return ResponseEntity.ok("Error checking image: " + e.getMessage());
        }
    }

    private String getContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            default:
                return "application/octet-stream";
        }
    }
}
