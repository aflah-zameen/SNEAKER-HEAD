package com.e_commerce.SNEAKERHEAD.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="wallet")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @OneToOne
    @JoinColumn(name="user_id")
    private WebUser user;

    @OneToMany(mappedBy = "wallet" , cascade = CascadeType.ALL)
    private List<Transaction> transactionList;

    @Column(name="balance")
    private Double balance;

    @Column(name="last_update")
    private LocalDate lastUpdate;



}
