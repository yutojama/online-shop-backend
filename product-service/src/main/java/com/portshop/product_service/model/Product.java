package com.portshop.product_service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.portshop.product_service.productEnum.Category;
import com.portshop.product_service.productEnum.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private String shopId;

    @Column(nullable = false)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    private Integer daysOfDelivery;

    @Column(nullable = false)
    private Integer origPrice;

    private Integer discountPrice;

    @Column(nullable = false)
    private String image;

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<Item> items = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void addItem(Item item){
        Item findItem = this.items.stream()
                .filter(it -> it.getSize().equals(item.getSize()))
                .findFirst()
                .orElse(null);
        if(findItem == null){
            items.add(item);
            item.setProduct(this);
        }else{
            findItem.setQuantity(findItem.getQuantity() + item.getQuantity());
        }
        this.quantity += item.getQuantity();
    }

    public void removeItem(Item item){
        if(items.contains(item)){
            items.remove(item);
            this.quantity -= item.getQuantity();
            item.setProduct(null);
        }
    }
}
