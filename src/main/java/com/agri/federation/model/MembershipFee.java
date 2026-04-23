package com.agri.federation.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate eligibleFrom;

    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    private double amount;

    private String label;

    @Enumerated(EnumType.STRING)
    private ActivityStatus status = ActivityStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "collectivite_id")
    @JsonIgnoreProperties({"membres", "president", "vicePresident", "tresorier", "secretaire", "membershipFees"})
    private Collectivite collectivite;
}
