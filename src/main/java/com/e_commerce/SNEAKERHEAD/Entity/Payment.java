package com.e_commerce.SNEAKERHEAD.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private WebUser user;

    @OneToOne
    @JoinColumn(name="transaction_id")
    private Transaction transaction;

    @Column(name="method")
    private String paymentMethod;

    @Column(name="status")
    private String status;

    @Column(name="date")
    private LocalDate paymentDate;

    @Column(name="updated_date")
    private LocalDate updateDate;

}
