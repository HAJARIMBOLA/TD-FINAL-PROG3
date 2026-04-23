package com.agri.federation.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collectivite {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;
    private LocalDate dateCreation;

    private boolean federationApproval;

    @Column(unique = true)
    private String uniqueNumber;

    @Column(unique = true)
    private String uniqueName;

    @ManyToOne
    @JoinColumn(name = "president_id")
    @JsonIgnoreProperties({"collectivite", "referees"})
    private Membre president;

    @ManyToOne
    @JoinColumn(name = "vice_president_id")
    @JsonIgnoreProperties({"collectivite", "referees"})
    private Membre vicePresident;

    @ManyToOne
    @JoinColumn(name = "tresorier_id")
    @JsonIgnoreProperties({"collectivite", "referees"})
    private Membre tresorier;

    @ManyToOne
    @JoinColumn(name = "secretaire_id")
    @JsonIgnoreProperties({"collectivite", "referees"})
    private Membre secretaire;

    @OneToMany(mappedBy = "collectivite")
    @JsonIgnoreProperties({"collectivite", "referees"})
    private List<Membre> membres;

    @OneToMany(mappedBy = "collectivite", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"collectivite"})
    private List<MembershipFee> membershipFees;
}