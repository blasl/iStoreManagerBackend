package com.kiyo.istoremanager.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "extra_expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtraExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String concept;

    @Column(nullable = false)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phone_id", nullable = false)
    private Phone phone;
}