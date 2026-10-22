package com.kiyo.istoremanager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal salePrice;

    @Column(nullable = false)
    private LocalDate saleDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phone_id", nullable = false, unique = true)
    private Phone phone;
}