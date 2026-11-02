package com.portshop.product_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.URLConnection;


@RestController
@CrossOrigin(origins = "${app.frontend.url}")
@Profile("aws")
public class ImageAwsController {

    // for aws env only
    @Value("${images.uri}")
    private String imageUri;
    private final RestClient restClient = RestClient.create();


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

}
