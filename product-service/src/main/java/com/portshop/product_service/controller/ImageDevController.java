package com.portshop.product_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@CrossOrigin(origins = "${app.frontend.url}")
@Profile("dev")
public class ImageDevController {

    private final Path imageDirectory;

    public ImageDevController(
            @Value("${images.uri}") String imagePath
    ) {
        this.imageDirectory = Paths.get(imagePath).toAbsolutePath().normalize();
    }


    @GetMapping("/public/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path file = imageDirectory.resolve(filename).normalize();
            if(!file.startsWith(imageDirectory)){
                return ResponseEntity.badRequest().build();
            }

            Resource resource = new UrlResource(file.toUri());

            if(!resource.exists()||!resource.isReadable()){
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(file);

            // fix if contentType cannot be read automatically
            if (contentType == null) {

                if (filename.endsWith(".avif")) {
                    contentType = "image/avif";
                } else if (filename.endsWith(".webp")) {
                    contentType = "image/webp";
                } else if (filename.endsWith(".png")) {
                    contentType = "image/png";
                } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else {
                    contentType = "application/octet-stream";
                }
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
