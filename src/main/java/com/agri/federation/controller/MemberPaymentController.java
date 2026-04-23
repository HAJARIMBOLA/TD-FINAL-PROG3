package com.agri.federation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.agri.federation.dto.CreateMemberPaymentRequest;
import com.agri.federation.model.MemberPayment;
import com.agri.federation.service.MemberPaymentService;

@RestController
@RequiredArgsConstructor
public class MemberPaymentController {

    private final MemberPaymentService service;

    // POST /members/{id}/payments
    @PostMapping("/members/{id}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public List<MemberPayment> createPayments(
            @PathVariable Long id,
            @RequestBody List<CreateMemberPaymentRequest> requests) {
        return service.createAll(id, requests);
    }
}
