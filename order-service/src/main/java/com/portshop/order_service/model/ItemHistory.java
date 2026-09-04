package com.portshop.order_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.portshop.order_service.orderEnum.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private OrderStatus fromStatus;

    @Column(nullable = false)
    private OrderStatus toStatus;

    @Column(nullable = false)
    private Instant changedAt;

    @Column(nullable = false)
    private String changedBy;

    @Column(nullable = false)
    private String reason;

    @ManyToOne
    @JoinColumn(name="item_change_record_id")
    @JsonBackReference
    private ItemChangeRecord itemChangeRecord;

}
