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
public class Membre {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address;
    private String profession;
    private String phoneNumber;
    private String email;

    @Enumerated(EnumType.STRING)
    private MemberOccupation occupation;

    private LocalDate dateAdhesion;

    @ManyToOne
    @JoinColumn(name = "collectivite_id")
    @JsonIgnoreProperties({"membres", "president", "vicePresident", "tresorier", "secretaire"})
    private Collectivite collectivite;

    @ManyToMany
    @JoinTable(
            name = "membre_parrains",
            joinColumns = @JoinColumn(name = "membre_id"),
            inverseJoinColumns = @JoinColumn(name = "parrain_id")
    )
    @JsonIgnoreProperties({"referees", "collectivite"})
    private List<Membre> referees;
}