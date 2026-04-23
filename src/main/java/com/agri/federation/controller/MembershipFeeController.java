package com.agri.federation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.agri.federation.dto.CreateMembershipFeeRequest;
import com.agri.federation.model.MembershipFee;
import com.agri.federation.service.MembershipFeeService;

@RestController
@RequiredArgsConstructor
public class MembershipFeeController {

    private final MembershipFeeService service;

    // GET /collectivities/{id}/membershipFees
    @GetMapping("/collectivities/{id}/membershipFees")
    public List<MembershipFee> getByCollectivite(@PathVariable Long id) {
        return service.getByCollectivite(id);
    }

    // POST /collectivities/{id}/membershipFees
    @PostMapping("/collectivities/{id}/membershipFees")
    @ResponseStatus(HttpStatus.OK)
    public List<MembershipFee> createMembershipFees(
            @PathVariable Long id,
            @RequestBody List<CreateMembershipFeeRequest> requests) {
        return service.createAll(id, requests);
    }
}
