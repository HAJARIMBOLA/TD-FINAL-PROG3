package com.agri.federation.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectivityTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate creationDate;

    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    @ManyToOne
    @JoinColumn(name = "compte_id")
    private Compte accountCredited;

    @ManyToOne
    @JoinColumn(name = "membre_id")
    @JsonIgnoreProperties({"referees", "collectivite"})
    private Membre memberDebited;

    @ManyToOne
    @JoinColumn(name = "collectivite_id")
    @JsonIgnoreProperties({"membres", "president", "vicePresident", "tresorier", "secretaire"})
    private Collectivite collectivite;
}
