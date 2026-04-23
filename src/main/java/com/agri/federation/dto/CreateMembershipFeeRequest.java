package com.agri.federation.dto;

import lombok.Data;
import java.time.LocalDate;
import com.agri.federation.model.Frequency;

@Data
public class CreateMembershipFeeRequest {

    private LocalDate eligibleFrom;
    private Frequency frequency;
    private double amount;
    private String label;
}
