package com.e_commerce.SNEAKERHEAD.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name="transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="date")
    private LocalDate transactionDate;

    @Column(name="amount")
    private Double amount;

    @Column(name="details")
    private String details;

    @Column(name="status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
}
