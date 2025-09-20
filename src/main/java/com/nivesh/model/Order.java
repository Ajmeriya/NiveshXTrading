package com.nivesh.model;

import com.nivesh.domain.OrderStatus;
import com.nivesh.domain.OrderType;
//import jakarta.persistence.*;
import jakarta.persistence.*;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;


@Data
@Entity
public class Order {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private BigDecimal price;

    private LocalDateTime timeStamp=LocalDateTime.now();

    @OneToOne(mappedBy = "order",cascade =CascadeType.ALL)
    private OrderItem orderItem;


}
