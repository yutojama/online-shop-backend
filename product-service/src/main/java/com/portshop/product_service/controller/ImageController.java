package com.portshop.product_service.controller;

import com.portshop.product_service.dto.ChangeImagePathRequestDTO;
import com.portshop.product_service.model.Product;
import com.portshop.product_service.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@CrossOrigin(origins = "${app.frontend.url}")
public class ImageController {

    private final Path imageDirectory;
    private final ProductService productService;

    // for aws env only
    @Value("${images.uri}")
    private String imageUri;
    private final RestClient restClient = RestClient.create();

    public ImageController(
            ProductService productService,
            @Value("${images.uri}") String imagePath
    ) {
        this.productService = productService;
        this.imageDirectory = Paths.get(imagePath).toAbsolutePath().normalize();
    }

//    @GetMapping("/public/images/{filename}")
//    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
//        try {
//            Path file = imageDirectory.resolve(filename).normalize();
//            if(!file.startsWith(imageDirectory)){
//                return ResponseEntity.badRequest().build();
//            }
//
//            Resource resource = new UrlResource(file.toUri());
//
//            if(!resource.exists()||!resource.isReadable()){
//                return ResponseEntity.notFound().build();
//            }
//
//            String contentType = Files.probeContentType(file);
//
//            // fix if contentType cannot be read automatically
//            if (contentType == null) {
//
//                if (filename.endsWith(".avif")) {
//                    contentType = "image/avif";
//                } else if (filename.endsWith(".webp")) {
//                    contentType = "image/webp";
//                } else if (filename.endsWith(".png")) {
//                    contentType = "image/png";
//                } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
//                    contentType = "image/jpeg";
//                } else {
//                    contentType = "application/octet-stream";
//                }
//            }
//
//            return ResponseEntity.ok()
//                    .contentType(MediaType.parseMediaType(contentType))
//                    .body(resource);
//
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }

    @GetMapping("/public/images/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            if (filename.contains("/") || filename.contains("\\")
                    || filename.equals(".") || filename.equals("..")) {
                return ResponseEntity.badRequest().build();
            }

            byte[] image = restClient.get()
                    .uri(imageUri + filename)
                    .retrieve()
                    .body(byte[].class);

            if (image == null) {
                return ResponseEntity.notFound().build();
            }

            String contentType = URLConnection
                    .guessContentTypeFromName(filename);

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(image);

        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PatchMapping("/api/images/change-paths")
    public ResponseEntity<?> changePaths(@RequestBody ChangeImagePathRequestDTO requestDTO) {
        List<Product> products = productService.findAll();
        products.forEach(product -> {
            Path imagePath = Paths.get(product.getImage());
            String imageName = imagePath.getFileName().toString();
            Path newImagePath = Paths.get(requestDTO.getNewPath(),imageName);
            product.setImage(newImagePath.toString());
        });
        productService.updateAll(products);

        return ResponseEntity.ok().body("Change image paths successful");
    }

}
