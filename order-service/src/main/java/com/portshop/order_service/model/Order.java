package com.portshop.order_service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.portshop.order_service.orderEnum.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String userKeycloakId;

    @Column(nullable = false)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal tax;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal shippingCost;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
    }

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<OrderStatusHistory> orderStatusHistories = new ArrayList<>();

    public void addOrderStatusHistory(OrderStatusHistory orderStatusHistory) {
        this.orderStatusHistories.add(orderStatusHistory);
    }


    @OneToOne
    @JoinColumn(name = "shipping_address_id")
    private Address shippingAddress;

    @OneToOne
    @JoinColumn(name = "billing_address_id")
    private Address billingAddress;


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
