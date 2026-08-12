package com.portshop.product_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer amountRemain;

    private Integer daysOfDelivery;

    @Column(nullable = false)
    private Integer origPrice;

    private Integer discountPrice;

    @Column(nullable = false)
    private String image;

}
