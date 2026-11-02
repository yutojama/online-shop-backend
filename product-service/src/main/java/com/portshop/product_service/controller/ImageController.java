package com.portshop.product_service.controller;

import com.portshop.product_service.dto.ChangeImagePathRequestDTO;
import com.portshop.product_service.model.Product;
import com.portshop.product_service.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@RestController
@CrossOrigin(origins = "${app.frontend.url}")
public class ImageController {

    private final ProductService productService;

    // for aws env only
    @Value("${images.uri}")
    private String imageUri;
    private final RestClient restClient = RestClient.create();

    public ImageController(
            ProductService productService
    ) {
        this.productService = productService;
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
