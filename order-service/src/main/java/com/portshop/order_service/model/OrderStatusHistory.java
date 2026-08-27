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
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name="order_id")
    @JsonBackReference
    private Order order;


    @Column(nullable = false)
    private OrderStatus fromStatus;

    @Column(nullable = false)
    private OrderStatus toStatus;

    @Column(nullable = false)
    private Instant changedAt;

    @Column(nullable = false)
    private String changedBy;

    @Column(nullable = false)
    private String reason;

}
