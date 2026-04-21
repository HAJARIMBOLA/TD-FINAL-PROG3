package com.agri.federation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public abstract class Compte {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double solde;
}