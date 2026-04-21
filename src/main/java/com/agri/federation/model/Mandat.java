package com.agri.federation.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
public class Mandat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @ManyToOne private Collectivite collectivite;

    @ManyToOne private Membre president;
    @ManyToOne private Membre tresorier;
    @ManyToOne private Membre secretaire;
}