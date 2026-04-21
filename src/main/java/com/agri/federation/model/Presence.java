package com.agri.federation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
public class Presence {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String statut;

    @ManyToOne
    private Membre membre;

    @ManyToOne
    private Activite activite;
}