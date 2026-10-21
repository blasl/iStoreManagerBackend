package com.kiyo.istoremanager.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "phones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String imei;

    @Column(nullable = false)
    private String model;

    private Integer batteryHealth;

    @Column(nullable = false)
    private BigDecimal purchasePrice;

    private BigDecimal salePrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhoneStatus status = PhoneStatus.DISPONIBLE;

    private LocalDate purchaseDate;

    private LocalDate saleDate;

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}


