package com.letocart.java_apirest_2026.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long ordersId;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String status; // "PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"

    // Relation ManyToOne avec Account
    // Un Orders appartient à un seul Account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // Relation OneToMany avec OrdersDetails
    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdersDetails> ordersDetails = new ArrayList<>();

    // Constructeurs
    public Orders() {
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
    }

    public Orders(Account account, BigDecimal totalAmount) {
        this.account = account;
        this.totalAmount = totalAmount;
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
    }

    // Getters et Setters
    public Long getOrdersId() { return ordersId; }
    public void setOrdersId(Long ordersId) { this.ordersId = ordersId; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public List<OrdersDetails> getOrdersDetails() { return ordersDetails; }
    public void setOrdersDetails(List<OrdersDetails> ordersDetails) { this.ordersDetails = ordersDetails; }
}
