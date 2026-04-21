package com.agri.federation.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
public class Activite {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private LocalDate date;
    private boolean obligatoire;

    @ManyToOne
    private Collectivite collectivite;
}