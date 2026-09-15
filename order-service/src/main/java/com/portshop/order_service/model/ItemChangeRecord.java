package com.portshop.order_service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemChangeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String shopId;

    @Column(nullable = false)
    private String itemId;

    @Column(nullable = false)
    private String orderId;

    @OneToMany(
            mappedBy = "itemChangeRecord",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    @Builder.Default
    List<ItemHistory> itemHistories = new ArrayList<>();

    public void addItemHistory(ItemHistory itemHistory) {
        itemHistories.add(itemHistory);
    }

}
