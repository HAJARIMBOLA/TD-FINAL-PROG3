package com.agri.federation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
public class CompteMobile extends Compte {

    private String titulaire;
    private String operateur;
    private String telephone;
}