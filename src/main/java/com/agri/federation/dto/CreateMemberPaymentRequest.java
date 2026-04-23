package com.agri.federation.dto;

import lombok.Data;
import com.agri.federation.model.PaymentMode;

@Data
public class CreateMemberPaymentRequest {

    private double amount;
    private Long membershipFeeIdentifier;
    private Long accountCreditedIdentifier;
    private PaymentMode paymentMode;
}
