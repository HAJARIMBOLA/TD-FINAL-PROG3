package com.agri.federation.dto;

import lombok.Data;

@Data
public class CotisationDTO {
    private double montant;
    private String modePaiement;
    private Long membreId;
}