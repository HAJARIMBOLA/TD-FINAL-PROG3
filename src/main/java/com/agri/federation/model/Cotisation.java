package com.agri.federation.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
public class Cotisation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double montant;
    private LocalDate datePaiement;
    private String modePaiement;

    @ManyToOne
    private Membre membre;
}