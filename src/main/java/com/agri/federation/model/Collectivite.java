package com.agri.federation.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Collectivite {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String ville;
    private String specialiteAgricole;
    private LocalDate dateCreation;

    @OneToMany(mappedBy = "collectivite")
    private List<Membre> membres;
}