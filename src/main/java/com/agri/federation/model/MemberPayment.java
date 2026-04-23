package com.agri.federation.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    @ManyToOne
    @JoinColumn(name = "compte_id")
    private Compte accountCredited;

    private LocalDate creationDate;

    @ManyToOne
    @JoinColumn(name = "membre_id")
    @JsonIgnoreProperties({"referees", "collectivite"})
    private Membre membre;

    @ManyToOne
    @JoinColumn(name = "membership_fee_id")
    @JsonIgnoreProperties({"collectivite"})
    private MembershipFee membershipFee;
}
